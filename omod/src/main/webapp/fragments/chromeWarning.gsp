
<script type="text/javascript">
    jq(document).ready(function() {

        var ua = window.navigator.userAgent;

        if (!/chrom(e|ium)/.test(ua.toLowerCase())) {
            jq('#chrome-warning-message').show();
        }
    });
</script>



<div id="chrome-warning-message" class="note-container" style="display:none">
    <div class="note error">
        <div class="text">
            <i class="icon-remove medium"></i>
            <!-- we are currently only using this on the login/home page, which appears before we know the user's context (and language preference) so the request has been to just hardcode to French -->
            <p>Veuillez utiliser un navigateur Chrome</p>
        </div>
        <div class="close-icon"><i class="icon-remove"></i></div>
    </div>
</div>
