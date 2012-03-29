package be.cytomine.test.http

import be.cytomine.ontology.Annotation
import be.cytomine.security.User
import be.cytomine.test.BasicInstance
import be.cytomine.test.HttpClient
import be.cytomine.test.Infos
import com.vividsolutions.jts.io.WKTReader
import grails.converters.JSON
import org.apache.commons.logging.LogFactory
import be.cytomine.social.SharedAnnotation

/**
 * User: lrollus
 * Date: 6/12/11
 * GIGA-ULg
 *
 */
class AnnotationCommentAPI extends DomainAPI {

    private static final log = LogFactory.getLog(this)

//    "/api/annotation/$annotation/comment"(controller:"restAnnotation"){
//        action = [POST: "addComment", GET:"listComments"]
//    }

    static def show(Long idAnnotation, Long idComment,String username, String password) {
        log.info "show comment $idComment for annotation $idAnnotation"
        String URL = Infos.CYTOMINEURL + "api/annotation/$idAnnotation/comment/$idComment" + ".json"
        HttpClient client = new HttpClient();
        client.connect(URL, username, password);
        client.get()
        int code = client.getResponseCode()
        String response = client.getResponseData()
        client.disconnect();
        return [data: response, code: code]
    }

    static def list(Long idAnnotation,String username, String password) {
        log.info "list comment by annotation $idAnnotation"
        String URL = Infos.CYTOMINEURL + "api/annotation/$idAnnotation/comment.json"
        HttpClient client = new HttpClient();
        client.connect(URL, username, password);
        client.get()
        int code = client.getResponseCode()
        String response = client.getResponseData()
        client.disconnect();
        return [data: response, code: code]
    }


    static def create(SharedAnnotation annotationCommentToAdd, User user) {
       create(annotationCommentToAdd.encodeAsJSON(),user.username,user.password)
    }

    static def create(SharedAnnotation annotationCommentToAdd, String username, String password) {
        return create(annotationCommentToAdd.encodeAsJSON(), username, password)
    }

    static def create(String jsonAnnotationComment, User user) {
        create(jsonAnnotationComment,user.username,user.password)
    }

    static def create(Long idAnnotation,String jsonAnnotationComment, String username, String password) {
        log.info("post annotation:" + jsonAnnotationComment.replace("\n", ""))
        String URL = Infos.CYTOMINEURL + "api/annotation/$idAnnotation/comment.json"
        HttpClient client = new HttpClient()
        client.connect(URL, username, password)
        client.post(jsonAnnotationComment)
        int code = client.getResponseCode()
        String response = client.getResponseData()
        println response
        client.disconnect();
        log.info("check response")
        def json = JSON.parse(response)
        Long idAnnotationComment = json?.sharedannotation?.id
        return [data: SharedAnnotation.get(idAnnotationComment), code: code]
    }
}