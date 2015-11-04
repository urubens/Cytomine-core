/*
* Copyright (c) 2009-2015. Authors: see NOTICE file.
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

class UserProjectConnectionUrlMappings {

    static mappings = {
        "/api/project/$project/userconnection.$format" (controller : "restUserProjectConnection") {
            action = [POST:"add"]
        }
        "/api/project/$project/userconnection/$user.$format" (controller : "restUserProjectConnection") {
            action = [GET:"getConnectionByUserAndProject"]
        }
        "/api/project/$project/lastconnections.$format" (controller : "restUserProjectConnection") {
            action = [GET:"lastConnectionInProject"]
        }
        "/api/project/$project/connectionFrequency.$format"(controller:"restUserProjectConnection") {
            action = [GET : "numberOfConnectionsByUserAndProject"]
        }
        "/api/project/$project/connectionFrequency/$user.$format"(controller:"restUserProjectConnection") {
            action = [GET : "numberOfConnectionsByUserAndProject"]
        }

    }
}
