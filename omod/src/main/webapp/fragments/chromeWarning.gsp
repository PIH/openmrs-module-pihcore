
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
            <p >${ ui.message("pihcore.chromeWarning")} </p>
        </div>
        <div class="close-icon"><i class="icon-remove"></i></div>
    </div>
</div>
