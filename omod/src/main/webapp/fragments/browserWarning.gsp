
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
            <!-- this is primarily used on the home page, before we know a user's locale, so the message text is
                 set in the implementation's config, not messages.properties -->
            <p>${ browserWarning }</p>
        </div>
        <div class="close-icon"><i class="icon-remove"></i></div>
    </div>
</div>
