<div class="row justify-content-between">
    <div class="col-8">
        <i class="fas fa-sign-in-alt"></i>
        <span ng-if="queueEntry.endedAt">
            {{ queueEntry.status.display }} - {{ queueEntry.queue.display }}
        </span>
        <span ng-if="!queueEntry.endedAt">
            <a href="/spa/home/service-queues">
                {{ queueEntry.status.display }} - {{ queueEntry.queue.display }}
            </a>
        </span>
        <span class="queue-entry-priority">
            <i ng-show="queueEntry.priority.uuid == Concepts.emergency.uuid" class="fas fa-exclamation-circle queue-entry-icon emergency"></i>
        </span>
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
