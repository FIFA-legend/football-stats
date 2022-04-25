let teams = null;

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe("/createUserDto/queue/teams/select", parseTeams);
});

setTimeout(function () {
    let isNeutralStadium = document.getElementById("is_neutral_stadium");
    let tournamentTag = document.getElementById("tournament");
    let neutralStadiumTag = document.getElementById("stadium");
    let dateTag = document.getElementById("date");
    let dateErrorsTag = document.getElementById("date_errors");
    let neutralStadiumErrorsTag = document.getElementById("neutral_stadium_errors");

    let homeSelect = document.getElementById("homeTeamId");
    let guestSelect = document.getElementById("guestTeamId");

    let flag1 = checkDateInFuture(dateTag, dateErrorsTag);
    let flag2 = checkNotEmptyTag(neutralStadiumTag, neutralStadiumErrorsTag);
    checkButton(flag1, flag2);

    dateTag.addEventListener("change", function () {
        flag1 = checkDateInFuture(dateTag, dateErrorsTag);
        checkButton(flag1, flag2);
    });

    neutralStadiumTag.addEventListener("keyup", function () {
        flag2 = checkNotEmptyTag(neutralStadiumTag, neutralStadiumErrorsTag);
        checkButton(flag1, flag2);
    });

    homeSelect.addEventListener("change", function () {
        manageTeams(homeSelect, guestSelect);
    });

    guestSelect.addEventListener("change", function () {
        manageTeams(guestSelect, homeSelect);
    });

    isNeutralStadium.addEventListener("click", function () {
        neutralStadiumClick(isNeutralStadium);
        checkButton(flag1, flag2);
    });
    tournamentTag.addEventListener("change", changeTournament);
    stomp.send("/websockets/teams/all");
}, 500);

let parseTeams = function (income) {
    let homeTeamSelect = document.getElementById("homeTeamId");
    let guestTeamSelect = document.getElementById("guestTeamId");
    while (homeTeamSelect.firstChild) {
        homeTeamSelect.removeChild(homeTeamSelect.firstChild);
    }
    while (guestTeamSelect.firstChild) {
        guestTeamSelect.removeChild(guestTeamSelect.firstChild);
    }
    teams = JSON.parse(income.body);
    for (let i = 0; i < teams.length; i++) {
        let homeTeamTag = document.createElement("option");
        let guestTeamTag = document.createElement("option");
        homeTeamTag.innerHTML = teams[i]["name"];
        homeTeamTag.value = teams[i]["id"];
        if (i !== 1) {
            homeTeamSelect.appendChild(homeTeamTag);
        }
        guestTeamTag.innerHTML = teams[i]["name"];
        guestTeamTag.value = teams[i]["id"];
        if (i !== 0) {
            guestTeamSelect.appendChild(guestTeamTag);
        }
    }
}

let changeTournament = function () {
    let tournamentTag = document.getElementById("tournament");
    let tournament = tournamentTag.value;
    if (tournament === "UCL" || tournament === "EUROPA_LEAGUE" || tournament === "FRIENDLY") {
        stomp.send("/websockets/teams/all");
    } else if (tournament === "EPL") {
        stomp.send("/websockets/teams/country", {}, "England");
    } else if (tournament === "LA_LIGA") {
        stomp.send("/websockets/teams/country", {}, "Spain");
    } else if (tournament === "BUNDESLIGA") {
        stomp.send("/websockets/teams/country", {}, "Germany");
    } else if (tournament === "LIGUE_1") {
        stomp.send("/websockets/teams/country", {}, "France");
    } else if (tournament === "LEGA_SERIE_A") {
        stomp.send("/websockets/teams/country", {}, "Italy");
    }
}

let manageTeams = function (tag, anotherTag) {
    let selectedTeamId = Number(tag.value);
    let selectedAnotherTeamId = Number(anotherTag.value);
    let selectedAnotherTeamName = anotherTag.children[anotherTag.selectedIndex].innerHTML;
    if (teams != null) {
        while (anotherTag.firstChild) {
            anotherTag.removeChild(anotherTag.firstChild);
        }
        let firstOptionTag = document.createElement("option");
        firstOptionTag.value = String(selectedAnotherTeamId);
        firstOptionTag.innerHTML = selectedAnotherTeamName;
        anotherTag.appendChild(firstOptionTag);
        for (let i = 0; i < teams.length; i++) {
            let optionTag = document.createElement("option");
            optionTag.value = teams[i]["id"];
            optionTag.innerHTML = teams[i]["name"];
            if (teams[i]["id"] !== selectedTeamId && teams[i]["id"] !== selectedAnotherTeamId) {
                anotherTag.appendChild(optionTag);
            }
        }
    }
}

let neutralStadiumClick = function (tag) {
    let neutralStadiumTag = document.getElementById("neutral_stadium");
    if (tag.checked) {
        neutralStadiumTag.classList.remove("disabled");
        neutralStadiumTag.classList.add("user_input");
    } else {
        neutralStadiumTag.classList.remove("user_input");
        neutralStadiumTag.classList.add("disabled");
    }
}

let checkDateInFuture = function (input, error) {
    if (input.value) {
        let date = new Date(input.value);
        let now = new Date();
        if (date < now) {
            error.innerHTML = "Date must be in future";
            return false;
        } else {
            error.innerHTML = "";
            return true;
        }
    } else {
        error.innerHTML = "Date must be in future";
        return false;
    }
}

let checkNotEmptyTag = function (input, error) {
    if (input.value.length <= 0) {
        error.innerHTML = "Not Empty";
        return false;
    } else {
        error.innerHTML = "";
        return true;
    }
}

let checkButton = function (flag1, flag2) {
    let button = document.getElementById("button");
    let switchTag = document.getElementById("is_neutral_stadium");

    if (switchTag.checked && flag1 && flag2) {
        button.disabled = false;
    } else {
        button.disabled = !(!switchTag.checked && flag1);
    }
}