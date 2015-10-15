<?php

$groupId = $_REQUEST['groupId'];
if ($groupId == null || $groupId == "") {
	echo "Missing param groupId";
	die;
}
$groupJson = file_get_contents("http://localhost:8081/groups/$groupId");
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
$serverIp = "50.28.32.145";
$socketIp = file_exists("on_server") ? $serverIp : $localIp;
?>
<html>
	<head>
		<meta charset="utf-8">
		<title>Payment organizer - <? echo $group->name;?></title>
		<script src="//code.jquery.com/jquery-1.11.3.min.js"></script>
		<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
		<script src="//cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js"></script>
		<script src="//cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
		<script>
		$(function() {
			$("#datepicker").datepicker();
			$("#datepicker").datepicker("option", "dateFormat", "dd.mm.yy.");
			$("#datepicker").datepicker('option', 'firstDay', 1);
			$("#datepicker").datepicker('setDate', new Date());
		});
		</script>
		<script>
			var socket = new SockJS('http://<?echo $socketIp;?>:8081/control');
			var stompClient = Stomp.over(socket);
			var stompFailureCallback = function (error) {
				console.log('STOMP: ' + error);
				setTimeout(stompConnect, 10000);
				console.log('STOMP: Reconecting in 10 seconds');
			};
			var stompSuccessCallback = function(frame) {
				//setConnected(true);
				console.log('Connected: ' + frame);
				stompClient.subscribe('<? echo $groupId; ?>', function(greeting){
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
		<h2>Group: <? echo $group->name;?></h2>
		<p>Status: <?
		if (is_array($group->sugestedTransactions)) {
			if (count($group->sugestedTransactions) > 0) {
				?> Suggested transactions:</p>
				<table>
				<?
					foreach ($group->sugestedTransactions as $sugestedTransaction) {
						?>
						<tr>
							<td><? echo $sugestedTransaction->from->name; ?></td>
							<td>&rarr;</td>
							<td><? echo round($sugestedTransaction->ammount, 2); ?></td>
							<td>&rarr;</td>
							<td><? echo $sugestedTransaction->to->name; ?></td>
							<td>
								<form action="creator.php" method="post" style="display:inline; margin:0;">
									<input type="hidden" name="type" value="Exchange">
									<input type="hidden" name="groupId" value="<?echo $group->id;?>">
									<input type="hidden" name="fromUserId" value="<?echo $sugestedTransaction->from->id;?>">
									<input type="hidden" name="toUserId" value="<?echo $sugestedTransaction->to->id;?>">
									<input type="hidden" name="ammount" value="<?echo $sugestedTransaction->ammount;?>">
									<input type="hidden" name="date" value="<? echo date("d.m.Y."); ?>">
									<input type="submit" name="add" value="Accept suggestion">
								</form>
							</td>
						</tr>
						<?
					}
				?>
				</table>
				<?
			} else {
				?> Everything is in balance</p><?
			}
		} else {
			?> group balance is <? echo round($group->groupBalance, 2);
			if ($group->groupBalance < 0) {
				?>, it is caused by missing payments or too may expenses.</p><?
			} else {
				?>, it is caused by missing expenses or too may payments.</p><?
			}
		}
		?>
		<p>Users:</p>
		<table border="1px">
			<tr><td>Name</td><td>Balance</td></tr>
			<?
			foreach ($group->userBalances as $userBalance) {
				?><tr>
					<td><? echo $userBalance->user->name; ?></td>
					<td><? 
					$balance = $userBalance->balance;
					$balance = abs($balance) < 0.01 ? 0 : $balance;
					echo round($balance, 2); 
					?></td>
				</tr><?
			}
			?>
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
			} else if (type == "Payment") {
				$('#paymentChooser').show();
				$('#expenseChooser').hide();
				$('#exchangeChooser').hide();
			} else if (type == "Expense") {
				$('#paymentChooser').hide();
				$('#expenseChooser').show();
				$('#exchangeChooser').hide();
			} else if (type == "Exchange") {
				$('#paymentChooser').hide();
				$('#expenseChooser').hide();
				$('#exchangeChooser').show();
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
			$('[name=ratio_'+element.value+']').prop('disabled', !element.checked);
			validate();
		}
		function exchangeChanged() {
			validate();
		}
		function isValid() {
			var valid = true;
			var dataValid = true;
			var ammount = $('#ammount').val();
			if (!(ammount > 0)) {
				$('#ammount').css("border-color","red");
				valid = false;
			} else {
				$('#ammount').css("border-color","green");
			}
			var invalid = true;
			var type = $('#typeChooser').val();
			if (type == "Payment") {
				var userId = $('input[name=userId]:checked').val();
				if (userId == undefined) {
					dataValid = false;
					valid = false;
				}
			} else if (type == "Expense") {
				var name = $('[name=name]').val();
				if (name.trim().length == 0) {
					$('[name=name]').css("border-color","red");
					dataValid = false;
					valid = false;
				} else {
					$('[name=name]').css("border-color","green");
				}
				var selectedUserIds = $('input[name=userIds\\[\\]]:checked').map(function () {
					return this.value;
				}).get();
				if (selectedUserIds.length == 0) {
					dataValid = false;
					valid = false;
				} else {
					var divisionType = $('input[name=distributionType]:checked').val();
					if (divisionType == 'custom') {
						var sum = 0;
						for (var i = 0; i < selectedUserIds.length; i++) {
							var userId = selectedUserIds[i];
							var ratio = parseFloat($('[name=ratio_'+userId+']').val());
							sum += ratio;
							if (!(ratio > 0) || !(ratio <= 1)) {
								$('[name=ratio_'+userId+']').css("border-color","red");
								valid = false;
							} else {
								$('[name=ratio_'+userId+']').css("border-color","green");
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
					$('#fromUserId').css("border-color","red");
					dataValid = false;
					valid = false;
				} else {
					$('#fromUserId').css("border-color","green");
				}
				var toUserId = $('#toUserId').val();
				if (toUserId == 'Choose') {
					$('#toUserId').css("border-color","red");
					dataValid = false;
					valid = false;
				} else {
					$('#toUserId').css("border-color","green");
				}
				if (fromUserId == toUserId) {
					dataValid = false;
					valid = false;
				}
			} else {
				valid = false;
			}
			if (!dataValid) {
				$('#dataTd').css("background-color","#faa");
				valid = false;
			} else {
				$('#dataTd').css("background-color","#afa");
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
			<input type="hidden" name="groupId" value="<? echo $groupId; ?>" />
			<tr>
				<td><input id="datepicker" type="text" name="date" value="<? echo date("d.m.Y."); ?>" size="11" style="background-color : #e1e1e1;"></td>
				<td>
					<select id="typeChooser" name="type" onchange="typeChanged();">
						<option value="Choose">Choose...</option>
						<option value="Expense">Expense</option>
						<option value="Payment">Payment</option>
						<option value="Exchange">Exchange</option>
					</select>
				</td>
				<td><input id="ammount" type="text" name="ammount" value="0" size="6" style="background-color : #e1e1e1;" onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();"></td>
				<td id="dataTd">
					<div id="paymentChooser" style="display:none;">
						<? 
						foreach ($group->users as $user) {
							?><span style="white-space: nowrap;"><?
							echo "<input type=\"radio\" name=\"userId\"  onchange=\"paymentChooseChanged();\" value=\"".$user->id."\">".$user->name;
							?></span><span> </span><?
						}
						?>
					</div>
					<div id="expenseChooser" style="display:none;">
						<p>What: <input type="text" name="name" onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();"/></p>
						<p>
						<input type="radio" name="distributionType" value="equal" onchange="divisionTypeChanged();" checked>Divide equal
						<input type="radio" name="distributionType" value="custom" onchange="divisionTypeChanged();">Custom
						<br>
						<? 
						$defaultFactor = round(1./count($group->users), 2);
						foreach ($group->users as $user) {
							?><span style="white-space: nowrap;"><?
							echo "<input type=\"checkbox\" onchange=\"expenseUserChanged(this);\" name=\"userIds[]\" value=\"".$user->id."\">".$user->name;
							?>
							<input class="userRatios" type="text" value="<?echo $defaultFactor?>" name="ratio_<?echo $user->id;?>" size="1" style="display:none;" disabled onchange="validate();" onkeypress="this.onchange();" onpaste="this.onchange();" oninput="this.onchange();">
							</span><span> </span><?
						}
						?>
						</p>
					</div>
					<div id="exchangeChooser" style="display:none;" >
						<select id="fromUserId" name="fromUserId" onchange="exchangeChanged();">
							<option value="Choose">Choose...</option>
							<?
							foreach ($group->users as $user) {
								echo "<option value=\"".$user->id."\">".$user->name."</option>";
							}
							?>
						</select>
						<span> to </span>
						<select  id="toUserId" name="toUserId" onchange="exchangeChanged();">
							<option value="Choose">Choose...</option>
							<?
							foreach ($group->users as $user) {
								echo "<option value=\"".$user->id."\">".$user->name."</option>";
							}
							?>
						</select>
					</div>
				</td>
				<td><input id="submit" type="submit" name="add" value="Add" disabled></td>
			</tr>
			</form>
			<? 
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
						$users[] = $userRatio->user->name . "(" . round($userRatio->ratio*100, 2) . "%)";
					}
					$data = "<strong>" . $paymentEvent->event->name . "</strong><br/>" . implode(", ", $users);
					$imgType = "expense-logo.jpg";
					$color = "#ffaaaa";
				}
				?>
				<tr>
				<td><? echo date("d.m.Y.", $paymentEvent->date/1000); ?></td>
				<td><img src="<? echo $imgType; ?>" width=25 height=25/><? echo $paymentEvent->type; ?></td>
				<td><? echo round($paymentEvent->event->ammount, 2); ?></td>
				<td><? echo $data; ?></td>
				<td>
					<form method="post" style="display: inline; margin: 0;" action="deleter.php" onsubmit="return confirm('Do you really want to delete <? echo $paymentEvent->type; ?>?');">
						<input type="hidden" name="id" value="<?echo $paymentEvent->event->id;?>" />
						<input type="hidden" name="type" value="<?echo $paymentEvent->type;?>" />
						<input type="hidden" name="groupId" value="<? echo $groupId; ?>" />
						<input type="image" src="delete-logo.jpg" alt="Delete" width="25" height="25" />
					</form>
				</td>
				</tr>
				<?
			}
			?>
		</table>
	</body>
</html>


