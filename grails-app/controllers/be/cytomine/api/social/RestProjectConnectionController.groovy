package be.cytomine.api.social


/*
* Copyright (c) 2009-2017. Authors: see NOTICE file.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import be.cytomine.Exception.CytomineException
import be.cytomine.api.RestController
import be.cytomine.project.Project
import be.cytomine.security.SecUser
import org.restapidoc.annotation.RestApiMethod
import org.restapidoc.annotation.RestApiParam
import org.restapidoc.annotation.RestApiParams
import org.restapidoc.pojo.RestApiParamType

import static org.springframework.security.acls.domain.BasePermission.READ

/**
 * Controller for an user connection to a project
 */
class RestProjectConnectionController extends RestController {

    def cytomineService
    def secUserService
    def projectService
    def projectConnectionService
    def imageConsultationService
    def securityACLService

    def add = {
        try {
            responseSuccess(projectConnectionService.add(request.JSON))
        } catch (CytomineException e) {
            log.error(e)
            response([success: false, errors: e.msg], e.code)
        }
    }

    def lastConnectionInProject = {
        Project project = projectService.read(params.project)
        Long userId = params.user ? Long.parseLong(params.user): null
        responseSuccess(projectConnectionService.lastConnectionInProject(project, userId));
    }

    def getConnectionByUserAndProject = {
        SecUser user = secUserService.read(params.user)
        Project project = projectService.read(params.project)
        Integer offset = params.offset != null ? params.getInt('offset') : 0
        Integer limit = params.limit != null ? params.getInt('limit') : -1
        def results = projectConnectionService.getConnectionByUserAndProject(user, project, limit, offset)
        // hack to avoid list to be cut. offset was already used in db request
        params.remove("offset")
        responseSuccess(results)
    }

    def numberOfConnectionsByProjectAndUser = {
        SecUser user = secUserService.read(params.user)
        Project project = projectService.read(params.project)
        Long afterThan = params.long("afterThan");
        String period = params.period

        if(params.boolean('heatmap')) {
            responseSuccess(projectConnectionService.numberOfConnectionsByProjectOrderedByHourAndDays(project, afterThan, user))
        }else if(period) {
            responseSuccess(projectConnectionService.numberOfProjectConnections(afterThan,period, project))
        } else {
            responseSuccess(projectConnectionService.numberOfConnectionsByProjectAndUser(project, user))
        }
    }

    def numberOfProjectConnections() {
        securityACLService.checkAdmin(cytomineService.getCurrentUser())
        Long afterThan = params.long("afterThan");
        String period = params.get("period").toString()
        if(period){
            responseSuccess(projectConnectionService.numberOfProjectConnections(afterThan,period))
        } else {
            response([success: false, message: "Mandatory parameter 'period' not found. Parameters are : "+params], 400)
        }
    }

    @RestApiMethod(description="Get the average project connections on Cytomine.")
    @RestApiParams(params=[
            @RestApiParam(name="afterThan", type="long", paramType = RestApiParamType.QUERY, description = "Average on the project connection where created > the afterThan parameter. Optional, the beforeThan Date -1 year will be considered if none is given."),
            @RestApiParam(name="beforeThan", type="long", paramType = RestApiParamType.QUERY, description = "Average on the project connection where created < the beforeThan parameter. Optional, the current Date will be considered if none is given."),
            @RestApiParam(name="period", type="string", paramType = RestApiParamType.QUERY, description = "The period of connections (hour : by hours, day : by days, week : by weeks) (Mandatory)"),
    ])
    def averageOfProjectConnections() {
        Long afterThan = params.long("afterThan");
        Long beforeThan = params.long("beforeThan");
        String period = params.get("period").toString()
        Project project = params.project ? projectService.read(params.project) : null;
        if(params.project){
            securityACLService.check(project,READ)
        } else{
            securityACLService.checkAdmin(cytomineService.getCurrentUser())
        }

        if(period){
            responseSuccess(projectConnectionService.averageOfProjectConnections(afterThan,beforeThan,period, project))
        } else {
            response([success: false, message: "Mandatory parameter 'period' not found. Parameters are : "+params], 400)
        }
    }

    @RestApiMethod(description="Get the project connections of one user into a project.")
    @RestApiParams(params=[
            @RestApiParam(name="user", type="long", paramType = RestApiParamType.PATH, description = "The user id. Mandatory"),
            @RestApiParam(name="project", type="long", paramType = RestApiParamType.PATH, description = "The project id. Mandatory"),
            @RestApiParam(name="offset", type="integer", paramType = RestApiParamType.QUERY, description = "An offset. Default value = 0"),
            @RestApiParam(name="limit", type="integer", paramType = RestApiParamType.QUERY, description = "Limit the project connections. Optionnal"),
    ])
    def userProjectConnectionHistory() {
        SecUser user = secUserService.read(params.user)
        Project project = projectService.read(params.project)
        Integer offset = params.offset != null ? params.getInt('offset') : 0
        Integer limit = params.limit != null ? params.getInt('limit') : -1

        // hack to avoid list to be cut. offset was already used in db request
        params.remove("offset")

        responseSuccess(projectConnectionService.getConnectionByUserAndProject(user, project, limit, offset))
    }
}
