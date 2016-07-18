<?php
$groupId = $_REQUEST['groupId'];
if ($groupId == null || $groupId == "") {
    echo "Missing param groupId";
    die;
}
$groupJson = file_get_contents("http://10.11.12.3:8081/groups/$groupId");
//var_dump($groupJson);
$group = json_decode($groupJson);
//var_dump($group);
$paymentEvents = array_reverse($group->paymentEvents);


if ($group == null) {
    echo "Group does not exist";
    die;
}
//hack to detect if running on server or local
$localIp = "127.0.0.1";
$serverIp = "213.186.1.203";
$socketIp = file_exists("on_server") ? $serverIp : $localIp;
?>
<html>
    <head>
        <meta charset="utf-8">
        <title>Payment organizer - <?php echo $group->name; ?></title>
        <script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
        <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
        <script src="//cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js"></script>
        <script src="//cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
        <script>
            $(function () {
                $("#datepicker").datepicker();
                $("#datepicker").datepicker("option", "dateFormat", "dd.mm.yy.");
                $("#datepicker").datepicker('option', 'firstDay', 1);
                $("#datepicker").datepicker('setDate', new Date());
            });
        </script>
        <script>
            var socket = new SockJS('http://<?php echo $socketIp; ?>:8081/control');
            var stompClient = Stomp.over(socket);
            var stompFailureCallback = function (error) {
                console.log('STOMP: ' + error);
                setTimeout(stompConnect, 10000);
                console.log('STOMP: Reconecting in 10 seconds');
            };
            var stompSuccessCallback = function (frame) {
                //setConnected(true);
                console.log('Connected: ' + frame);
                stompClient.subscribe('<?php echo $groupId; ?>', function (greeting) {
                    showGreeting(greeting.body);
                });
            };
            function stompConnect() {
                console.log('STOMP: Attempting connection');
                // recreate the stompClient to use a new WebSocket
                //stompClient = Stomp.over(socket);
                console.log('STOMP: Attempting new connect');
                stompClient.connect({}, stompSuccessCallback, stompFailureCallback);
            }
            function connect() {
                stompConnect();
            }
            function showGreeting(message) {
                location.reload();
            }
        </script>
        <style type="text/css">
            .ui-datepicker {
                background: #FFF;
                border: 2px solid #555;
                color: #000;
            }
            #ui-datepicker-div { display: none; }
        </style>
    </head>
    <body onload="connect();">
        <h2>Group: <?php echo $group->name; ?></h2>
        <p>Status: <?php
            if (is_array($group->sugestedTransactions)) {
                if (count($group->sugestedTransactions) > 0) {
                    ?> Suggested transactions:</p>
                <table>
                    <?php
                    foreach ($group->sugestedTransactions as $sugestedTransaction) {
                        ?>
                        <tr>
                            <td><?php echo $sugestedTransaction->from->name; ?></td>
                            <td>&rarr;</td>
                            <td><?php echo number_format($sugestedTransaction->ammount, 2); ?></td>
                            <td>&rarr;</td>
                            <td><?php echo $sugestedTransaction->to->name; ?></td>
                            <td>
                                <form action="creator.php" method="post" style="display:inline; margin:0;">
                                    <input type="hidden" name="type" value="Exchange">
                                    <input type="hidden" name="groupId" value="<?php echo $group->id; ?>">
                                    <input type="hidden" name="fromUserId" value="<?php echo $sugestedTransaction->from->id; ?>">
                                    <input type="hidden" name="toUserId" value="<?php echo $sugestedTransaction->to->id; ?>">
                                    <input type="hidden" name="ammount" value="<?php echo $sugestedTransaction->ammount; ?>">
                                    <input type="hidden" name="date" value="<?php echo date("d.m.Y."); ?>">
                                    <input type="submit" name="add" value="Accept suggestion">
                                </form>
                            </td>
                        </tr>
                        <?php
                    }
                    ?>
                </table>
                <?php
            } else {
                ?> Everything is in balance</p><?php
        }
    } else {
        ?> group balance is <?php
            echo number_format($group->groupBalance, 2);
            if ($group->groupBalance < 0) {
                ?>, it is caused by 1) missing payments or 2) too may expenses or 3) missing incomes or 4) too many receivements.</p><?php
    } else {
        ?>, it is caused by 1) missing expenses or 2) too may payments or 3) missing receivements or 4) too many incomes.</p><?php
    }
}
?>
<p>Users:</p>
<table border="1px">
    <tr><td>Name</td><td>Balance</td></tr>
    <?php
    foreach ($group->userBalances as $userBalance) {
        ?><tr>
            <td><?php echo $userBalance->user->name; ?></td>
            <td><?php
                $balance = $userBalance->balance;
                $balance = abs($balance) < 0.01 ? 0 : $balance;
                echo number_format($balance, 2);
                ?>
            </td>
            <td>
                <a href="userDashboard.php?groupId=<?php echo $groupId; ?>&userId=<?php echo $userBalance->user->id; ?>">
                    Show history</i>
                </a>
            </td>
        </tr><?php
    }
    ?>
</table>
<br>
<table border="1px">
    <tr><td>All expenses</td><td>All incomes</td></tr>
    <tr>
        <td><?php echo number_format($group->groupStats->sumOfExpenses, 2); ?></td>
        <td><?php echo number_format($group->groupStats->sumOfIncomes, 2); ?></td>
    </tr>
</table>
<p>Payment events:</p>
<table border="1px">
    <col>
    <col>
    <col>
    <col width="250">
    <col>
    <tr>
        <td>Date</td>
        <td>Type</td>
        <td>Ammount</td>
        <td>Data</td>
        <td>Action</td>
    </tr>
    <script>
        function typeChanged() {
            var type = $('#typeChooser').val();
            if (type == "Choose") {
                $('#paymentChooser').hide();
                $('#expenseChooser').hide();
                $('#exchangeChooser').hide();
                $('#incomeChooser').hide();
                $('#receivementChooser').hide();
            } else if (type == "Payment") {
                $('#paymentChooser').show();
                $('#expenseChooser').hide();
                $('#exchangeChooser').hide();
                $('#incomeChooser').hide();
                $('#receivementChooser').hide();
            } else if (type == "Expense") {
                $('#paymentChooser').hide();
                $('#expenseChooser').show();
                $('#exchangeChooser').hide();
                $('#incomeChooser').hide();
                $('#receivementChooser').hide();
            } else if (type == "Exchange") {
                $('#paymentChooser').hide();
                $('#expenseChooser').hide();
                $('#exchangeChooser').show();
                $('#incomeChooser').hide();
                $('#receivementChooser').hide();
            } else if (type == "Income") {
                $('#paymentChooser').hide();
                $('#expenseChooser').hide();
                $('#exchangeChooser').hide();
                $('#incomeChooser').show();
                $('#receivementChooser').hide();
            } else if (type == "Receivement") {
                $('#paymentChooser').hide();
                $('#expenseChooser').hide();
                $('#exchangeChooser').hide();
                $('#incomeChooser').hide();
                $('#receivementChooser').show();
            }
            validate();
        }
        function paymentChooseChanged() {
            validate();
        }
        function divisionTypeChanged() {
            var divisionType = $('input[name=distributionType]:checked').val();
            if (divisionType == 'equal') {
                $('.userRatios').hide();
            } else if (divisionType == 'custom') {
                $('.userRatios').show();
            }
            validate();
        }
        function expenseUserChanged(element) {
            $('[name=ratio_' + element.value + ']').prop('disabled', !element.checked);
            validate();
        }
        function incomeUserChanged(element) {
            $('[name=ratio_' + element.value + ']').prop('disabled', !element.checked);
            validate();
        }
        function exchangeChanged() {
            validate();
        }
        function receivementChooseChanged() {
            validate();
        }
        function isValid() {
            var valid = true;
            var dataValid = true;
            var ammount = $('#ammount').val();
            if (!(ammount > 0)) {
                $('#ammount').css("border-color", "red");
                valid = false;
            } else {
                $('#ammount').css("border-color", "green");
            }
            var invalid = true;
            var type = $('#typeChooser').val();
            if (type == "Payment") {
                var userId = $('#paymentChooser input[name=userId]:checked').val();
                if (userId == undefined) {
                    dataValid = false;
                    valid = false;
                }
            } else if (type == "Expense") {
                var name = $('#expenseChooser [name=expenseName]').val();
                if (name.trim().length == 0) {
                    $('#expenseChooser [name=expenseName]').css("border-color", "red");
                    dataValid = false;
                    valid = false;
                } else {
                    $('#expenseChooser [name=expenseName]').css("border-color", "green");
                }
                var selectedUserIds = $('#expenseChooser input[name=userIds\\[\\]]:checked').map(function () {
                    return this.value;
                }).get();
                if (selectedUserIds.length == 0) {
                    dataValid = false;
                    valid = false;
                } else {
                    var divisionType = $('#expenseChooser input[name=distributionType]:checked').val();
                    if (divisionType == 'custom') {
                        var sum = 0;
                        for (var i = 0; i < selectedUserIds.length; i++) {
                            var userId = selectedUserIds[i];
                            var ratio = parseFloat($('#expenseChooser [name=ratio_' + userId + ']').val());
                            sum += ratio;
                            if (!(ratio > 0) || !(ratio <= 1)) {
                                $('#expenseChooser [name=ratio_' + userId + ']').css("border-color", "red");
                                valid = false;
                            } else {
                                $('#expenseChooser [name=ratio_' + userId + ']').css("border-color", "green");
                            }
                        }
                        if (Math.abs(sum - 1) > 1e-14) {
                            dataValid = false;
                            valid = false;
                        }
                    }
                }
            } else if (type == "Exchange") {
                var fromUserId = $('#fromUserId').val();
                if (fromUserId == 'Choose') {
                    $('#exchngeChooser #fromUserId').css("border-color", "red");
                    dataValid = false;
                    valid = false;
                } else {
                    $('#exchngeChooser #fromUserId').css("border-color", "green");
                }
                var toUserId = $('#exchngeChooser #toUserId').val();
                if (toUserId == 'Choose') {
                    $('#exchngeChooser #toUserId').css("border-color", "red");
                    dataValid = false;
                    valid = false;
                } else {
                    $('#exchngeChooser #toUserId').css("border-color", "green");
                }
                if (fromUserId == toUserId) {
                    dataValid = false;
                    valid = false;
                }
            } else if (type == "Income") {
                var name = $('#incomeChooser [name=incomeName]').val();
                if (name.trim().length == 0) {
                    $('#incomeChooser [name=incomeName]').css("border-color", "red");
                    dataValid = false;
                    valid = false;
                } else {
                    $('#incomeChooser [name=incomeName]').css("border-color", "green");
                }
                var selectedUserIds = $('#incomeChooser input[name=userIds\\[\\]]:checked').map(function () {
                    return this.value;
                }).get();
                if (selectedUserIds.length == 0) {
                    dataValid = false;
                    valid = false;
                } else {
                    var divisionType = $('#incomeChooser input[name=distributionType]:checked').val();
                    if (divisionType == 'custom') {
                        var sum = 0;
                        for (var i = 0; i < selectedUserIds.length; i++) {
                            var userId = selectedUserIds[i];
                            var ratio = parseFloat($('#incomeChooser [name=ratio_' + userId + ']').val());
                            sum += ratio;
                            if (!(ratio > 0) || !(ratio <= 1)) {
                                $('#incomeChooser [name=ratio_' + userId + ']').css("border-color", "red");
                                valid = false;
                            } else {
                                $('#incomeChooser [name=ratio_' + userId + ']').css("border-color", "green");
                            }
                        }
                        if (Math.abs(sum - 1) > 1e-14) {
                            dataValid = false;
                            valid = false;
                        }
                    }
                }
            } else if (type == "Receivement") {
                var userId = $('#receivementChooser input[name=userId]:checked').val();
                if (userId == undefined) {
                    dataValid = false;
                    valid = false;
                }
            } else {
                valid = false;
            }
            if (!dataValid) {
                $('#dataTd').css("background-color", "#faa");
                valid = false;
            } else {
                $('#dataTd').css("background-color", "#afa");
            }
            return valid;
        }
        function validate() {
            var valid = isValid();
            if (!valid) {
                document.getElementById("submit").disabled = true;
            } else {
                document.getElementById("submit").disabled = false;
            }
        }
    </script>
    <form id="addForm" action="creator.php" method="post">
        <input type="hidden" name="groupId" value="<?php echo $groupId; ?>" />
        <tr>
            <td><input id="datepicker" type="text" name="date" value="<?php echo date("d.m.Y."); ?>" size="11" style="background-color : #e1e1e1;"></td>
            <td>
                <select id="typeChooser" name="type" onchange="typeChanged();">
                    <option value="Choose">Choose...</option>
                    <option value="Expense">Expense</option>
                    <option value="Payment">Payment</option>
                    <option value="Exchange">Exchange</option>
                    <option value="Income">Income</option>
                    <option value="Receivement">Receivement</option>
                </select>
            </td>
            <td><input id="ammount" type="text" name="ammount" value="0" size="6" style="background-color : #e1e1e1;" onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();"></td>
            <td id="dataTd">
                <div id="paymentChooser" style="display:none;">
                    <?php
                    foreach ($group->users as $user) {
                        ?><span style="white-space: nowrap;"><?php
                        echo "<input type=\"radio\" name=\"userId\"  onchange=\"paymentChooseChanged();\" value=\"" . $user->id . "\">" . $user->name;
                        ?></span><span> </span><?php
                    }
                    ?>
                </div>
                <div id="expenseChooser" style="display:none;">
                    <p>What: <input type="text" name="expenseName" onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();"/></p>
                    <p>
                        <input type="radio" name="distributionType" value="equal" onchange="divisionTypeChanged();" checked>Divide equal
                        <input type="radio" name="distributionType" value="custom" onchange="divisionTypeChanged();">Custom
                        <br>
                        <?php
                        $defaultFactor = round(1. / count($group->users), 2);
                        foreach ($group->users as $user) {
                            ?><span style="white-space: nowrap;"><?php
                            echo "<input type=\"checkbox\" onchange=\"expenseUserChanged(this);\" name=\"userIds[]\" value=\"" . $user->id . "\">" . $user->name;
                            ?>
                                <input class="userRatios" type="text" value="<?php echo $defaultFactor ?>" name="ratio_<?php echo $user->id; ?>" size="1" style="display:none;" disabled onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();">
                            </span><span> </span><?php
                        }
                        ?>
                    </p>
                </div>
                <div id="exchangeChooser" style="display:none;" >
                    <select id="fromUserId" name="fromUserId" onchange="exchangeChanged();">
                        <option value="Choose">Choose...</option>
                        <?php
                        foreach ($group->users as $user) {
                            echo "<option value=\"" . $user->id . "\">" . $user->name . "</option>";
                        }
                        ?>
                    </select>
                    <span> to </span>
                    <select  id="toUserId" name="toUserId" onchange="exchangeChanged();">
                        <option value="Choose">Choose...</option>
                        <?php
                        foreach ($group->users as $user) {
                            echo "<option value=\"" . $user->id . "\">" . $user->name . "</option>";
                        }
                        ?>
                    </select>
                </div>
                <div id="incomeChooser" style="display:none;">
                    <p>What: <input type="text" name="incomeName" onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();"/></p>
                    <p>
                        <input type="radio" name="distributionType" value="equal" onchange="divisionTypeChanged();" checked>Divide equal
                        <input type="radio" name="distributionType" value="custom" onchange="divisionTypeChanged();">Custom
                        <br>
                        <?php
                        $defaultFactor = round(1. / count($group->users), 2);
                        foreach ($group->users as $user) {
                            ?><span style="white-space: nowrap;"><?php
                            echo "<input type=\"checkbox\" onchange=\"incomeUserChanged(this);\" name=\"userIds[]\" value=\"" . $user->id . "\">" . $user->name;
                            ?>
                                <input class="userRatios" type="text" value="<?php echo $defaultFactor ?>" name="ratio_<?php echo $user->id; ?>" size="1" style="display:none;" disabled onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();">
                            </span><span> </span><?php
                        }
                        ?>
                    </p>
                </div>
                <div id="receivementChooser" style="display:none;">
                    <?php
                    foreach ($group->users as $user) {
                        ?><span style="white-space: nowrap;"><?php
                        echo "<input type=\"radio\" name=\"userId\"  onchange=\"receivementChooseChanged();\" value=\"" . $user->id . "\">" . $user->name;
                        ?></span><span> </span><?php
                    }
                    ?>
                </div>

            </td>
            <td><input id="submit" type="submit" name="add" value="Add" disabled></td>
        </tr>
    </form>
    <?php
    foreach ($paymentEvents as $paymentEvent) {
        $data = "???";
        $imgType = "";
        $color = "#fffff";
        if ($paymentEvent->type == "Payment") {
            $data = $paymentEvent->event->user->name;
            $imgType = "payment-logo.jpg";
            $color = "#aaffaa";
        } else if ($paymentEvent->type == "Exchange") {
            $data = $paymentEvent->event->from->name . " &rarr; " . $paymentEvent->event->to->name;
            $imgType = "exchange-logo.jpg";
            $color = "#aaaaff";
        } else if ($paymentEvent->type == "Expense") {
            $users = array();
            foreach ($paymentEvent->event->userRatios as $userRatio) {
                $users[] = $userRatio->user->name . "(" . round($userRatio->ratio * 100, 2) . "%)";
            }
            $data = "<strong>" . $paymentEvent->event->name . "</strong><br/>" . implode(", ", $users);
            $imgType = "expense-logo.jpg";
            $color = "#ffaaaa";
        } else if ($paymentEvent->type == "Income") {
            $users = array();
            foreach ($paymentEvent->event->userRatios as $userRatio) {
                $users[] = $userRatio->user->name . "(" . round($userRatio->ratio * 100, 2) . "%)";
            }
            $data = "<strong>" . $paymentEvent->event->name . "</strong><br/>" . implode(", ", $users);
            $imgType = "income-logo.png";
            $color = "#ffaaaa";
        } else if ($paymentEvent->type == "Receivement") {
            $data = $paymentEvent->event->user->name;
            $imgType = "receivement-logo.png";
            $color = "#aaffaa";
        }
        ?>
        <tr>
            <td><?php echo date("d.m.Y.", $paymentEvent->date / 1000); ?></td>
            <td><img src="<?php echo $imgType; ?>" width=25 height=25/><?php echo $paymentEvent->type; ?></td>
            <td align="right"><?php echo number_format($paymentEvent->event->ammount, 2); ?></td>
            <td><?php echo $data; ?></td>
            <td>
                <form method="post" style="display: inline; margin: 0;" action="deleter.php" onsubmit="return confirm('Do you really want to delete <?php echo $paymentEvent->type; ?>?');">
                    <input type="hidden" name="id" value="<?php echo $paymentEvent->event->id; ?>" />
                    <input type="hidden" name="type" value="<?php echo $paymentEvent->type; ?>" />
                    <input type="hidden" name="groupId" value="<?php echo $groupId; ?>" />
                    <input type="image" src="delete-logo.jpg" alt="Delete" width="25" height="25" />
                </form>
            </td>
        </tr>
        <?php
    }
    ?>
</table>
</body>
</html>


