<div class="row justify-content-between">
    <div class="col-8">
        <i class="icon-list"></i>  {{ queueEntry.status.display }} - {{ queueEntry.queue.display }}
    </div>
    <div class="col-4 text-right">
        <span class="date-span">
            <i class="icon-calendar"></i>{{ queueEntry.startedAt | serverDateLocalized:DatetimeFormats.dateLocalized }}
        </span>
        <span class="time-span">
            <i class="icon-time"></i>{{ queueEntry.startedAt | serverDateLocalized:DatetimeFormats.timeLocalized }}
        </span>
        <div ng-show="queueEntry.endedAt">
            <span class="date-span">
                <i class="icon-calendar"></i>{{ queueEntry.endedAt | serverDateLocalized:DatetimeFormats.dateLocalized }}
            </span>
            <span class="time-span">
                <i class="icon-time"></i>{{ queueEntry.endedAt | serverDateLocalized:DatetimeFormats.timeLocalized }}
            </span>
        </div>
    </div>
</div>
