let daysAgo = -10;
let now = Date.now();
let matches = null;
const oneDay = 86400000;
let emptyResponse = 0;

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe("/websockets/matches/get", processIncome);
    stomp.subscribe("/match/score/update", processScoreUpdate);
});

setTimeout(function () {
    window.addEventListener("scroll", processScroll);
}, 1000);

let processScroll = function () {
    let windowRelativeBottom = document.documentElement.getBoundingClientRect().bottom;
    if (windowRelativeBottom < document.documentElement.clientHeight + 100) {
        processMatches(new Date(now - oneDay * daysAgo));
        daysAgo++;
    }
}

let processIncome = function (income) {
    matches = JSON.parse(income.body);
    processMatches(new Date(now - oneDay * daysAgo));
}

let processScoreUpdate = function (income) {
    let match = JSON.parse(income.body);
    let main = document.getElementById("main_content");
    a: for (let i = 0; i < main.children.length; i++) {
        let p = main.children.item(i).children.item(0);
        let dateLine = p.innerHTML;
        for (let j = 1; j < main.children.item(i).children.length; j += 2) {
            let a = main.children.item(i).children.item(j);
            let matchLine = a.innerHTML;
            let words = matchLine.split("\t");
            if (words[0] === match["homeTeam"]["name"] && words[2] === match["guestTeam"]["name"] && dateLine === dateToString(new Date(match["date"]))) {
                a.innerHTML = match["homeTeam"]["name"] + "\t" + countGoals(match["events"], true) + ":" +
                    countGoals(match["events"], false) + "\t" + match["guestTeam"]["name"];
                break a;
            }
        }
    }
}

let processMatches = function (date) {
    const mainHolder = document.getElementById("main_content");
    let div = document.createElement("div");
    let count = 0;
    for (let i = 0; i < matches.length; i++) {
        if (dateToString(new Date(matches[i]["date"])) === dateToString(date)) {
            if (count === 0) {
                let dateHolder = document.createElement("p");
                dateHolder.innerHTML = dateToString(new Date(Number(matches[i]["date"])));
                div.appendChild(dateHolder);
                count++;
            }
            div.appendChild(formLink(matches[i]));
            div.appendChild(document.createElement("br"));
        }
    }
    if (div.children.length > 0) {
        appendMatches(mainHolder, div);
    }
    if (document.documentElement.getBoundingClientRect().bottom < document.documentElement.clientHeight + 100) {
        if (emptyResponse < 90) {
            emptyResponse++;
            processMatches(new Date(now - oneDay * daysAgo++));
        }
    }
}

let formLink = function (match) {
    let tag = document.createElement("a");
    tag.setAttribute("href", "/match/" + match["id"]);
    tag.classList.add("match_result");
    let homeTeam = match["homeTeam"]["name"];
    let homeTeamScore = "-";
    if (match["events"]) {
        homeTeamScore = countGoals(match["events"], true);
    }
    let guestTeam = match["guestTeam"]["name"];
    let guestTeamScore = "-";
    if (match["events"]) {
        guestTeamScore = countGoals(match["events"], false);
    }
    tag.innerHTML = homeTeam + "\t" + homeTeamScore + ":" + guestTeamScore + "\t" + guestTeam;
    return tag;
}

let countGoals = function (events, home) {
    let count = 0;
    for (let i = 0; i < events.length; i++) {
        if ((events[i]["eventType"] === "GOAL" || events[i]["eventType"] === "PENALTY") && events[i]["homeTeam"] === home) {
            count++;
        }
    }
    return count;
}

let appendMatches = function (mainTag, divTag) {
    if (mainTag.children.length === 0) {
        mainTag.appendChild(divTag);
    } else {
        let isAppended = 0;
        let divDate = divTag.children.item(0).innerHTML;
        for (let i = 1; i < mainTag.children.length; i++) {
            let previousDate = mainTag.children.item(i - 1).children.item(0).innerHTML;
            let nextDate = mainTag.children.item(i).children.item(0).innerHTML;
            if (divDate < nextDate && divDate > previousDate) {
                mainTag.children.item(i).prepend(divTag);
                isAppended = 1;
                break;
            }
        }
        if (isAppended === 0) {
            mainTag.appendChild(divTag);
        }
    }
}

let dateToString = function (date) {
    let stringDate = date.getFullYear() + "-";
    if ((date.getMonth() + 1) >= 1 && (date.getMonth() + 1) <= 9) {
        stringDate += "0";
    }
    stringDate += (date.getMonth() + 1) + "-";
    if (date.getDate() >= 1 && date.getDate() <= 9) {
        stringDate += "0";
    }
    stringDate += date.getDate();
    return stringDate;
}