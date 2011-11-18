package be.cytomine

import be.cytomine.ontology.Annotation
import be.cytomine.security.User
import be.cytomine.ontology.AnnotationTerm
import be.cytomine.project.Project
import be.cytomine.image.ImageInstance
import be.cytomine.ontology.Term
import be.cytomine.command.annotationterm.AddAnnotationTermCommand
import be.cytomine.Exception.ObjectNotFoundException
import grails.converters.JSON
import be.cytomine.command.annotationterm.DeleteAnnotationTermCommand

class AnnotationTermService {

    static transactional = true
    def cytomineService
    def transactionService
    def commandService

    def list(Annotation annotation) {
        annotation.annotationTerm
    }

    def listNotUser(Annotation annotation, User user) {
        AnnotationTerm.findAllByAnnotationAndUserNotEqual(annotation, user)
    }

    def list(ImageInstance image,Term term) {

        def annotations = []
        Annotation.findAllByImage(image).each { annotation ->
            annotation.annotationTerm.each { annotationTerm ->
                if (annotationTerm.getTerm() == term) annotations << annotation
            }
        }
        annotations
    }


    def read(Annotation annotation, Term term, User user){
        if(user) AnnotationTerm.findWhere('annotation': annotation, 'term': term, 'user': user)
        else AnnotationTerm.findByAnnotationAndTerm(annotation, term)
    }

    def addAnnotationTerm(def json) {
        User currentUser = cytomineService.getCurrentUser()
        json.user = currentUser.id
        commandService.processCommand(new AddAnnotationTermCommand(user: currentUser), json)
    }

   def addAnnotationTerm(def idAnnotation, def idTerm, def idUser, User currentUser) {
       def json = JSON.parse("{annotation: $idAnnotation, term: $idTerm, user: $idUser}")
       commandService.processCommand(new AddAnnotationTermCommand(user: currentUser), json)
   }

     def deleteAnnotationTerm(def idannotation, def idterm){
        User currentUser = cytomineService.getCurrentUser()
        return deleteAnnotationTerm(idannotation, idterm, currentUser.id, currentUser)
    }
    /**
     * Add annotation-term for an annotation and delete all annotation-term that where already map with this annotation by this user
     */
    def addWithDeletingOldTerm(def idAnnotation, def idterm) {
        User currentUser = cytomineService.getCurrentUser()
            Annotation annotation = Annotation.read(idAnnotation)
            if(!annotation) throw new ObjectNotFoundException("Annotation $idAnnotation not found")
            //Start transaction
            transactionService.start()

            //Delete all annotation term
            deleteAnnotationTermFromUser(annotation, currentUser, currentUser)

            //Delete annotation term
            def result = deleteAnnotationTerm(idAnnotation, idterm, currentUser.id, currentUser)

            //Stop transaction
            transactionService.stop()

            return result
    }

    /**
     * Delete an annotation term
     */
    def deleteAnnotationTerm(def idAnnotation, def idTerm, def idUser, User currentUser) {
        return deleteAnnotationTerm(idAnnotation,idTerm,idUser,currentUser,true)
    }
    def deleteAnnotationTerm(def idAnnotation, def idTerm, def idUser, User currentUser, boolean printMessage) {
        def json = JSON.parse("{annotation: $idAnnotation, term: $idTerm, user: $idUser}")
        def result = commandService.processCommand(new DeleteAnnotationTermCommand(user: currentUser,printMessage: printMessage), json)
        return result
    }

    /**
     * Delete all term map by user for annotation
     */
    def deleteAnnotationTermFromUser(Annotation annotation, User user, User currentUser) {
        //Delete all annotation term
        def annotationTerm = AnnotationTerm.findAllByAnnotationAndUser(annotation, user)
        log.info  "Delete old annotationTerm= " + annotationTerm.size()

        annotationTerm.each { annotterm ->
            log.info  "unlink annotterm:" + annotterm.id
            deleteAnnotationTerm(annotterm.annotation.id, annotterm.term.id, annotterm.user.id, currentUser,false)
        }
    }

    /**
     * Delete all term map for annotation
     */
    def deleteAnnotationTermFromAllUser(Annotation annotation, User currentUser) {
        //Delete all annotation term
        def annotationTerm = AnnotationTerm.findAllByAnnotation(annotation)
        log.info  "Delete old annotationTerm= " + annotationTerm.size()

        annotationTerm.each { annotterm ->
            log.info  "unlink annotterm:" + annotterm.id
            deleteAnnotationTerm(annotterm.annotation.id, annotterm.term.id, annotterm.user.id, currentUser,false)
        }
    }

    /**
     * Delete all term map by user for term
     */
    def deleteAnnotationTermFromAllUser(Term term, User currentUser) {
        //Delete all annotation term
        def annotationTerm = AnnotationTerm.findAllByTerm(term)
        log.info  "Delete old annotationTerm= " + annotationTerm.size()

        annotationTerm.each { annotterm ->
            log.info  "unlink annotterm:" + annotterm.id
            deleteAnnotationTerm(annotterm.annotation.id, annotterm.term.id, annotterm.user.id, currentUser,false)
        }
    }





















}
