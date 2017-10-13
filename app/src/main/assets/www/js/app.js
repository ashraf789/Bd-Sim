function isNativeApp () {
    return /SimManager\/[0-9\.]+$/.test(navigator.userAgent);
}

function formatPhoneNumber(phoneNumber) {
    var number = phoneNumber.replace(/[\s-]/g, '').match(/[\d]{11}$/);
    if (number)
        return number[0];
}

$(function () {
    $('a[href]').click(function (e) {
        var href = $(this).attr('href');
        if(href == '#')
            return;

        if (href.indexOf('tel:') === 0 || href.indexOf('mailto:') === 0 || href.indexOf('http') === 0) return;

        if (isNativeApp()) {
            e.preventDefault();

            var url = location.href.replace(/[a-z\-]+\.html$/g, $(this).attr('href'));
            //Android.startActivity(intent);
            Android.navigateTo(url);
        }
    });

    $(document).on('click', 'a[data-ussd]', function (e) {
        e.preventDefault();

        var button = $(this);
        var ussd = button.attr('data-ussd');
        var message = button.attr('data-confirm');
        if (message) {
            if (isNativeApp()) {
                Android.dialUSSDWithConfirmation(ussd, 'Confirmation', message);
            }
            else {
                alert('Title: ' + 'Confirmation' + '\nMessage: ' + message);
            }

            return;
        }

        if (ussd == '*#06#') {
            var imei = isNativeApp() ? Android.getDeviceId() : '1234567890';

            /*jshint multistr: true */
            var html = '<div id="imei-window" class="modal fade">\
  <div class="modal-dialog">\
    <div class="modal-content">\
      <div class="modal-header">\
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>\
        <h4 class="modal-title">IMEI Number</h4>\
      </div>\
      <div class="modal-body">\
        <h2 class="text-center">' + imei + '</h2>\
      </div>\
      <div class="modal-footer">\
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>\
      </div>\
    </div><!-- /.modal-content -->\
  </div><!-- /.modal-dialog -->\
</div><!-- /.modal -->';
            $('body').append(html);
            var mWindow = $('#imei-window');
            mWindow.modal('show');

            return;
        }

        var tr = button.closest('tr');

        if (tr.length === 0 || $(this).hasClass('free')) {
            if (isNativeApp()) { Android.dialUSSD(ussd); }
            else { alert('USSD: ' + ussd); }
            return;
        }

        var table = button.closest('table');
        if (table.find('thead th[data-field]').length > 0) {
            var field_names = ['validity', 'charge', 'volume', 'offer'];
            var info = {};

            var topButton = true;
            if (table.find('thead th[rowspan]').length > 0) {
                topButton = button.closest('tr').index() % 2 === 0;
            }

            for (var i in field_names) {
                var name = field_names[i];
                var index_x = table.find('thead th[data-field=' + name + ']').index();
                
                if (index_x < 0) continue;

                var index_y = table.find('thead tr th[data-field=' + name + ']').parent().index();

                var selectedTr = tr;
                if (index_y === 1 && topButton) selectedTr = tr.next();
                if (index_y === 0 && !topButton) selectedTr = tr.prev();

                info[name] = selectedTr.find('td:nth(' + index_x + ')').text().trim();
            }

            message = 'Warning: It will cost you BDT ' + info.charge + '.';

            var title = 'Confirmation';
            if (info.offer) {
                message += ' Selected offer: ' + info.offer + '.';
            }
            else {
                title = info.volume + ' Pack (' + info.validity + ')';
            }

            message += ' Are you sure you want to continue?';
            
            if (isNativeApp()) {
                Android.dialUSSDWithConfirmation(ussd, title, message);
            }
            else {
                alert('Title: ' + title + '\nMessage: ' + message + '\nUSSD: ' + ussd);
            }

            return;
        }

        var params = getUSSDParameters(button);

        if (isNativeApp()) {
            Android.dialUSSDWithConfirmation(ussd, params.title, params.message);
        }
        else {
            alert('Title: ' + params.title + '\nMessage: ' + params.message + '\nUSSD: ' + ussd);
        }
    });

    $(document).on('click', 'a[data-message]:not([disabled])', function (e) {
        e.preventDefault();

        var button = $(this);
        var number = button.attr('data-number');
        var message = button.attr('data-message');

        if (isNativeApp()) {
            Android.sendSMS(number, message);
        }
        else {
            alert('Send To: ' + number + '\nMessage: ' + message);
        }
    });

    if (isNativeApp()) {
        //Android.setTitle($('head > title').text());
    }

    $('[data-toggle="popover"]').popover();
});

function getGroupedLog(json) {
    var data = [];

    for (i = 0; i < json.length; i++) {
        var number = json[i].number;
        number = number.replace(/[\s]/g, '').match(/[\d]{11}$/g);
        if (number === null || json[i].type !== 2) continue;
        json[i].number = number[0];
        data.push(json[i]);
    }

    var result = data.reduce(function (res, obj) {
        if (!(obj.number in res))
            res.__array.push(res[obj.number] = obj);
        else {
            res[obj.number].duration += obj.duration;

            if (obj.name !== null)
                res[obj.number].name = obj.name;
        }
        return res;
    }, { __array: [] }).__array
        .sort(function (a, b) { return b.duration - a.duration; });

    return result;
}

/* angular */
if (typeof angular != 'undefined') {
    angular.module('appModule', []).filter('phoneText', ['$sce', function ($sce) {
        return function (input) {
            var output = input;

            // USSD filter
            output = output.replace(/(\*[^#]+#)/g, '<a class="btn btn-default btn-xs" data-ussd="$1" data-confirm="Are you sure you want to dial $1?">$1</a>');

            // URL filter
            output = output.replace(/(http[^ ,]+)/g, '<a href="$1">$1</a>');

            // Number filter
            output = output.replace(/([ :])([\d]{3}[\d]+)/, '$1<a href="tel:$2" class="btn btn-default btn-xs">$2</a>');

            return $sce.trustAsHtml(output);
        };
    }]);

    var app = angular.module('simApp', ['appModule']);
    app.controller('smsListController', function ($scope, $http, $attrs) {
        if (isNativeApp()) {
            // type, address, service_center
            $scope.smsList = angular.fromJson(Android.getSmsList('inbox', $attrs.criteria, eval($attrs.parameters))); // jshint ignore:line
        }
        else {
            console.log('Criteria: ' + $attrs.criteria);
            console.log('Parameters: ' + $attrs.parameters);

            $http.get('../data/sms-inbox.json').then(function (response) {
                $scope.smsList = response.data;
            });
        }
    });

    app.controller('callLogController', function ($scope, $http) {
        $scope.formatDuration = function (duration) {
            if (duration < 60) return duration + ' s';
            duration /= 60;
            duration = parseInt(duration);
            if (duration < 60) return duration + ' m';

            duration /= 60;
            duration = parseInt(duration);
            return duration + ' h';

            /*duration /= 24;
            duration = parseInt(duration);

            return duration + ' d';*/
        };

        //$scope = Android.getCallLogs();
        if (isNativeApp()) {
            //$scope.callLogsStr = Android.getCallLogs();
            $scope.callLogs = getGroupedLog(angular.fromJson(Android.getCallLogs()));
        }
        else {
            var json = angular.fromJson('[{ "name": "", "number": "+8801819442052", "duration": 100000, "type": 2 }, { "name": "Rakib", "number": "01819442052", "duration": 10, "type": 2 }, { "name": "Rakib", "number": "01819442051", "duration": 10, "type": 2 }]');
            $scope.callLogs = getGroupedLog(json);
        }
    });
}