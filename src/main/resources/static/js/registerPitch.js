$(document).ready(function () {
    // $.validator.addMethod('nameCheck', function(value, element, param) {
    //     var nameRegex = /^[a-zA-Z]+$/;
    //     return value.match(nameRegex);
    // }, 'Vui lòng nhập đúng định dạng tên (VD:Nguyễn Văn A)');

    var val = {
        // Specify validation rules
        rules: {
            name:"required",
            mail: {
                required: true,
                email: true,
            },
            phone: {
                required: true,
                minlength: 10,
                maxlength: 10,
                digits: true,
            },
            bank: "required",
            stkbank: {
                required: true,
                digits: true,
            },
            namePitch:"required",
            district: "required",
            address: "required",
            pic1: "required",
            pic2: "required",
            pic3: "required",
        },
        // Specify validation error messages
        messages: {
            name: "*Trường này là bắt buộc",

            mail: {
                required: "*Trường này là bắt buộc",
                email: "Vui lòng nhập đúng định dạng e-mail (VD:example.com)",
            },
            phone: {
                required: "*Trường này là bắt buộc",
                minlength: "Số điện thoại gồm 10 ký tự",
                maxlength: "Số điện thoại gồm 10 ký tự",
                digits: "Vui lòng chỉ nhập số",
            },
            bank: "*Trường này là bắt buộc",
            stkbank: {
                required: "*Trường này là bắt buộc",
                digits: "Vui lòng chỉ nhập số",
            },
            namePitch: "*Trường này là bắt buộc",
            district: "*Trường này là bắt buộc",
            address: "*Trường này là bắt buộc",
            pic1: "*Trường này là bắt buộc",
            pic2: "*Trường này là bắt buộc",
            pic3: "*Trường này là bắt buộc",
        },
    };
    $("#myForm")
        .multiStepForm({
            // defaultStep:0,
            beforeSubmit: function (form, submit) {
                console.log("called before submiting the form");
                console.log(form);
                console.log(submit);
            },
            validations: val,
        })
        .navigateTo(0);
});

(function ($) {
    $.fn.multiStepForm = function (args) {
        if (args === null || typeof args !== "object" || $.isArray(args))
            throw " : Called with Invalid argument";
        var form = this;
        var tabs = form.find(".tab");
        var steps = form.find(".step");
        steps.each(function (i, e) {
            $(e).on("click", function (ev) {});
        });
        form.navigateTo = function (i) {
            /*index*/
            /*Mark the current section with the class 'current'*/
            tabs.removeClass("current").eq(i).addClass("current");
            // Show only the navigation buttons that make sense for the current section:
            form.find(".previous").toggle(i > 0);
            atTheEnd = i >= tabs.length - 1;
            form.find(".next").toggle(!atTheEnd);
            // console.log('atTheEnd='+atTheEnd);
            form.find(".submit").toggle(atTheEnd);
            fixStepIndicator(curIndex());
            return form;
        };
        function curIndex() {
            /*Return the current index by looking at which section has the class 'current'*/
            return tabs.index(tabs.filter(".current"));
        }
        function fixStepIndicator(n) {
            steps.each(function (i, e) {
                i == n ? $(e).addClass("active") : $(e).removeClass("active");
            });
        }
        /* Previous button is easy, just go back */
        form.find(".previous").click(function () {
            tabs.removeClass("from-right");
            tabs.addClass("from-left");
            form.navigateTo(curIndex() - 1);
        });

        /* Next button goes forward iff current block validates */
        form.find(".next").click(function () {
            tabs.removeClass("from-left");
            tabs.addClass("from-right");
            if (
                "validations" in args &&
                typeof args.validations === "object" &&
                !$.isArray(args.validations)
            ) {
                if (
                    !("noValidate" in args) ||
                    (typeof args.noValidate === "boolean" && !args.noValidate)
                ) {
                    form.validate(args.validations);
                    if (form.valid() == true && $('#alert-phone-exist').length == 0) {

                        form.navigateTo(curIndex() + 1);
                        return true;
                    }
                    return false;
                }
            }
            form.navigateTo(curIndex() + 1);
        });
        form.find(".submit").on("click", function (e) {
            if (
                typeof args.beforeSubmit !== "undefined" &&
                typeof args.beforeSubmit !== "function"
            )
                args.beforeSubmit(form, this);
            /*check if args.submit is set false if not then form.submit is not gonna run, if not set then will run by default*/
            if (
                typeof args.submit === "undefined" ||
                (typeof args.submit === "boolean" && args.submit)
            ) {
                form.submit();
            }
            return form;
        });
        /*By default navigate to the tab 0, if it is being set using defaultStep property*/
        typeof args.defaultStep === "number"
            ? form.navigateTo(args.defaultStep)
            : null;

        form.noValidate = function () {};
        return form;
    };
})(jQuery);

//image preview
function previewImage(event, id){
    if(event.target.files.length > 0){
        var preview = document.getElementById(id);
        if (event.target.files[0].size > 1048576) {
            showNotify("Vui lòng chọn ảnh có dung lượng dưới 1mb!", "alert-danger");
            event.target.value = "";
            preview.src = "";
            preview.style.display = "none";
            return;
        }
        var src = URL.createObjectURL(event.target.files[0]);
        preview.src = src;
        preview.style.display = "block";
    }
}


$("input[name='phone']").first().change(function () {
    var phone = $("input[name='phone']").first().val();
    $('#alert-phone-exist').remove();
    $("input[name='phone']").first().css("border-color", "#ced4da");
    $("input[name='phone']").first().css("box-shadow", "none");
    if (phone.length == 10) {
        $.ajax('/getphonenumber/' + phone,   // request url
            {
                success: function (data, status, xhr) {// success callback function
                    // $("#getTimeAndPrice").empty();
                    if (data != '') {
                        if ($('#alert-phone-exist').length == 0) {
                            $("input[name='phone']").first().css("border-color", "#ff7373");
                            $("input[name='phone']").first().css("box-shadow", "0 0 4px #d53030");
                            $("input[name='phone']").first().after(`
                                            <span id="alert-phone-exist" style="color: #f00; font-style: italic; font-size: .8em">Số điện thoại đã được sử dụng!</span>
                                            `);
                        }
                    }
                }
            });
    }
});
$("input[name='phone']").first().on("input", function () {
    $('#alert-phone-exist').remove();
    $("input[name='phone']").first().css("border-color", "#ced4da");
    $("input[name='phone']").first().css("box-shadow", "0 0 0 .2rem rgba(0, 123, 255, .25)");

});
$("input[name='phone']").first().focus(function () {
    if ($('#alert-phone-exist').length == 0) {
        $("input[name='phone']").first().css("box-shadow", "0 0 0 .2rem rgba(0, 123, 255, .25)");
    }
});
$("input[name='phone']").first().blur(function () {
    // $('.next').first().css("pointer-events", 'auto');
    var phone = $("input[name='phone']").first().val();
    if (phone.length == 10) {
        $.ajax('/getphonenumber/' + phone,   // request url
            {
                success: function (data, status, xhr) {// success callback function
                    // $("#getTimeAndPrice").empty();
                    if (data != '') {
                        if ($('#alert-phone-exist').length == 0) {
                            $("input[name='phone']").first().css("border-color", "#ff7373");
                            $("input[name='phone']").first().css("box-shadow", "0 0 4px #d53030");
                            $("input[name='phone']").first().after(`
                                            <span id="alert-phone-exist" style="color: #f00; font-style: italic; font-size: .8em">Số điện thoại đã được sử dụng!</span>
                                            `);
                        }

                    }
                }
            });
    }
    if ($('#alert-phone-exist').length == 0) {
        $("input[name='phone']").first().css("box-shadow", "none");
    }
});

