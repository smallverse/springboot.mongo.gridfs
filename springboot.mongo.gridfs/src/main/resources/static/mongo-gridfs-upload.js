var MongoGridfsUpload = {
    options: {
        url: "",

    },
    fileInputs: null,
    formData: null,
    files: null,
    init: function() {
        var me = this;

        me.fileInputs = $(".mongo-gridfs-upload");
        me.files = me.getFiles();
        me.formData = me.getFormData();
    },
    getFiles: function() {
        var me = this;

        var files = [];
        if (!me.fileInputs) {
            console.error("fileInputs is null");
            return files;
        }

        for (var i = 0; i < me.fileInputs.length; i++) {
            const input = me.fileInputs[i];
            for (var j = 0; j < input.files.length; j++) {
                files.push(input.files[j]);
            }
        }

        return files;
    },
    getFormData: function() {
        var me = this;

        var formData = new FormData();
        for (var i = 0; i < me.files.length; i++) {
            formData.append("file", me.files[i]);
        }

        return formData;
    },
    upload: function(url, call) {
        var me = this;

        me.init();
        if (!url) {
            console.error("url is null");
            return;
        }

        $.ajax({
            type: "post",
            url: url,
            contentType: false,
            processData: false,
            data: me.formData,
            success: function(data) {
                if (call && typeof call == "function") {
                    call(data)
                }
            },
            error: function(data) {
                console.debug(data);
            },
            complete: function(data) {
                console.debug(data);
            }
        });
    },
    uploadByOptions: function(options) {
        var me = this;

        me.options = options || {};
        me.options.btnId = options.btnId || "mongo-gridfs-submit";
        me.options.fileId = options.fileId || "upload";
        $(document).on('change', '#' + options.fileId, function(e) {
            _doUpload(options);
        });
        $(document).on("click", '#' + options.btnId, function() {
            $('#' + options.fileId).click();
        });
    }
};