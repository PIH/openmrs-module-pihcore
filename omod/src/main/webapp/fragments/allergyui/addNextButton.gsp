
<script type="text/javascript">
    jq(document).ready(function() {

        // super-hack to insert the "Next" button into allergies page
        jq('#allergyui-addNewAllergy').before('<button id="next" type="button" class="submit confirm right" style="margin-left:10px">${ui.message('emr.next')}<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>');


        jq('#next').click(function() {
            window.location.href = '${ ui.escapeJs(returnUrl) }' + '&goToNextSection=allergies';
        })

    });
</script>