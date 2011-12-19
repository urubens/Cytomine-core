var ShareAnnotationView = Backbone.View.extend({
    tagName : "div",
    initialize: function(options) {
        this.image = options.image;
        this.project = options.project;
    },

    doLayout: function(shareAnnotationViewTpl, shareAnnotationMailTpl) {
        var self = this;
        this.model.set({ "username" :  window.app.models.users.get(this.model.get("user")).prettyName()});
        this.model.set({ "terms" :  "undefined"});
        var dialog = new ConfirmDialogView({
            el:'#dialogs',
            template : _.template(shareAnnotationViewTpl, this.model.toJSON()),
            dialogAttr : {
                dialogID : "#share-confirm"
            }
        }).render();
        var userCollection = new UserCollection({project : this.project}).fetch({
            success : function (collection, response) {
                userCollection = collection;
                var optionTpl = "<option value='<%= id %>'><%= name %></option>"
                collection.each(function (user) {
                    selectUser.append(_.template(optionTpl, { id : user.id, name : user.prettyName()}));
                });
            },
            error : function(collection, response) {
                window.app.view.message("Error", response.message, "error")
            }
        });

        var selectUser = $("#selectUserShare" + self.model.id);
        var shareWithOption = $("input[name=shareWithOption]");

        var getSelectedUsers = function () {
            var value = $("input[name=shareWithOption]:checked").val();
            if (value == "everyone") {
                var users = []
                userCollection.each (function (user) {
                    users.push(user.get("id"));
                });
                return users;
            } else if (value == "somebody") {
                return [selectUser.val()];
            }
        }

        shareWithOption.change(function(){
            var value = $(this).val();
            if (value == "everyone") {
                selectUser.attr("disabled", "disabled");
            } else if (value == "somebody") {
                selectUser.removeAttr("disabled");
            }
        });

        $("#shareCancelButton"+self.model.id).click(function(){
            self.close();
            return false;
        });

        $("#shareButton"+self.model.id).click(function(){
            var shareButton = $(this);
            shareButton.html("Sending...");

            var users = getSelectedUsers();
            var userName = (_.size(users) == 1) ? userCollection.get(users[0]).prettyName() : "user";
            var comment = $("#annotationComment"+self.model.id).val();
            var annotationURL = _.template(window.app.status.serverURL+"/#tabs-image-<%= idProject %>-<%= idImage %>-<%= idAnnotation %>",
                { idProject : self.project,
                    idImage : self.image,
                    idAnnotation : self.model.id
                });
            var message = _.template(shareAnnotationMailTpl, {
                from : userCollection.get(window.app.status.user.id).prettyName(),
                to : userName,
                comment : comment,
                annotationURL : annotationURL,
                by : window.app.status.serverURL
            });
            var subject = _.template("Cytomine : <%= from %> shared an annotation with you",{ from : userCollection.get(window.app.status.user.id).prettyName()});
            new ShareAnnotationModel().save({
                users : users,
                annotation : self.model.id,
                message : message,
                comment : comment,
                subject : subject,
                annotationURL : annotationURL
            }, {
                success : function (model, response) {
                    shareButton.html("Share");
                    window.app.view.message("Success", response.message, "success");
                    self.close();
                },
                error : function (model, response) {
                    shareButton.html("Share");
                    window.app.view.message("Error", response.message, "error");
                }
            });
            return false;
        });

        return this;
    },
    render: function() {
        var self = this;
        require(["text!application/templates/annotation/ShareAnnotationView.tpl.html",
            "text!application/templates/annotation/ShareAnnotationMail.tpl.html"], function(shareAnnotationViewTpl, shareAnnotationMailTpl) {
            self.doLayout(shareAnnotationViewTpl, shareAnnotationMailTpl);
        });
    },
    close : function() {
        $("#share-confirm").modal('hide');
        $("#share-confirm").remove();
        window.history.back();
    }
});