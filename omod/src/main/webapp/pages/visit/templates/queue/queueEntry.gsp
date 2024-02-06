<div class="row justify-content-between">
    <div class="encounter-type encounter-name col-8">
        <i ng-show="icon" class="{{ icon }}"></i> {{ queueEntry.queue.display }}
    </div>
    <div class="col-4 text-right">
        <span class="date-span">
            <i class="icon-calendar"></i>{{ queueEntry.startedAt | serverDateLocalized:DatetimeFormats.dateLocalized }}
        </span>
        <span class="time-span">
            <i class="icon-time"></i>{{ queueEntry.startedAt | serverDateLocalized:DatetimeFormats.timeLocalized }}
        </span>
    </div>
</div>
