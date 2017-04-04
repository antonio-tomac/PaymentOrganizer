<?php
$groupId = $_REQUEST['groupId'];
if ($groupId == null || $groupId == "") {
    echo "Missing param groupId";
    die;
}
$userId = $_REQUEST['userId'];
if ($userId == null || $userId == "") {
    echo "Missing param userId";
    die;
}
$userEventsJson = file_get_contents("http://10.11.12.3:8081/groups/$groupId/users/$userId/events");
//var_dump($groupJson);
$userEvents = array_reverse(json_decode($userEventsJson));
?>
<html>
    <head>
        <meta charset="utf-8">
        <title>Payment organizer user events</title>
    </head>
    <body>
        <p>Payment events:</p>
        <table border="1px">
            <col>
            <col>
            <col>
            <col>
            <col>
            <col width="250">
            <col>
            <tr>
                <td>Impact</td>
                <td>Balance</td>
                <td>Date</td>
                <td>Type</td>
                <td>Amount</td>
                <td>Data</td>
                <td>Action</td>
            </tr>
            <?php
            foreach ($userEvents as $userEvent) {
                $impact = number_format($userEvent->impact, 2);
                $balance = number_format($userEvent->balance, 2);
                $paymentEvent = $userEvent->paymentEvent;
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
                    <td align="right"><?php echo $impact; ?></td>
                    <td align="right"><?php echo $balance; ?></td>
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


