package be.cytomine.test.http

import be.cytomine.security.User
import be.cytomine.test.HttpClient
import be.cytomine.test.Infos
import grails.converters.JSON
import org.apache.commons.logging.LogFactory

/**
 * User: lrollus
 * Date: 6/12/11
 * GIGA-ULg
 * This class implement all method to easily get/create/update/delete/manage User to Cytomine with HTTP request during functional test
 */
class UserAPI extends DomainAPI {

    private static final log = LogFactory.getLog(this)

    static def showCurrent(String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user/current.json"
        return doGET(URL, username, password)
    }

    static def grid(String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user/grid.json"
        return doGET(URL, username, password)
    }

    static def show(Long id, String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user/" + id + ".json"
        return doGET(URL, username, password)
    }

    static def showUserJob(Long id, String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/userJob/" + id + ".json"
        return doGET(URL, username, password)
    }

    static def list(String username, String password) {
        list(null,username,password)
    }

    static def list(String key,String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user.json" + (key? "?publicKey=$key":"")
        return doGET(URL, username, password)
    }

    static def list(Long id,String domain,String type,String username, String password) {
        list(id,domain,type,false,username,password)
    }

    static def list(Long id,String domain,String type,def offline,String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/${domain}/$id/${type}.json" + (offline? "?offline=true":"")
        return doGET(URL, username, password)
    }

    static def listFriends(Long id,def offline, Long idProject,String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user/$id/friends.json?offline=" + (offline? "true":"false") + (idProject? "&project=${idProject}":"")
        return doGET(URL, username, password)
    }

    static def listOnline(Long id,String username, String password) {
        String URL = Infos.CYTOMINEURL + "/api/project/$id/online/user"
        return doGET(URL, username, password)
    }

    static def listUserJob(Long id,Boolean tree, Long idImage,String username, String password) {
        String URL = Infos.CYTOMINEURL + "/api/project/$id/userjob?tree="+(tree?"true":false)+(idImage?"&image=$idImage":"")
        return doGET(URL, username, password)
    }

    static def create(String json, String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user.json"
        def result = doPOST(URL,json,username,password)
        result.data = User.get(JSON.parse(result.data)?.user?.id)
        return result
    }

    static def update(def id, def jsonUser, String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user/" + id + ".json"
        return doPUT(URL,jsonUser,username,password)
    }

    static def delete(def id, String username, String password) {
        String URL = Infos.CYTOMINEURL + "api/user/" + id + ".json"
        return doDELETE(URL,username,password)
    }
}
