const teamsUrl = '/queue/teams/return';

const prefix = '/websockets';
const firstPageUrl = '/teams/start';
const nextPageUrl = '/teams/next';
const previousPageUrl = '/teams/previous';
const deletePageUrl = '/teams/delete/';
const updatePageUrl = '/team/update/';

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe('/user' + teamsUrl, processTeams);
});

setTimeout(function () {
    let previousButton = document.getElementById("previous_button");
    let nextButton = document.getElementById("next_button");
    clearTableBody();

    stomp.send(prefix + firstPageUrl, {});
    previousButton.addEventListener("click", function () {
        stomp.send(prefix + previousPageUrl, {});
    });
    nextButton.addEventListener("click", function () {
        stomp.send(prefix + nextPageUrl, {});
    });
}, 500);

function processTeams(income) {
    clearTableBody();
    let teams = JSON.parse(income.body);
    let tbody = document.getElementsByTagName("tbody")[0];
    for (let i = 0; i < teams.length; i++) {
        let tr = document.createElement("tr");
        addLineInTable(teams[i], tr);
        tbody.appendChild(tr);
    }
}

let clearTableBody = function () {
    let tbody = document.getElementsByTagName("tbody")[0];
    while (tbody.lastChild) {
        tbody.removeChild(tbody.lastChild);
    }
}

let addLineInTable = function (team, tr) {
    let teamFullName = document.createElement("td");
    let teamShortName = document.createElement("td");
    let teamCountry = document.createElement("td");
    let upd = document.createElement("td");
    let del = document.createElement("td");

    tr.appendChild(teamFullName);
    tr.appendChild(teamShortName);
    tr.appendChild(teamCountry);
    tr.appendChild(upd);
    tr.appendChild(del);

    teamFullName.innerHTML = team["fullName"] + ' ';
    teamShortName.innerHTML = team["shortName"];
    teamCountry.innerHTML = team["country"]["name"];

    if (team["hasLogo"]) {
        const img = document.createElement("img");
        img.src = '/team/' + team["id"] + '/image';
        img.width = 30;
        img.height = 30;
        teamFullName.appendChild(img);
    }

    let updButton = document.createElement("button");
    updButton.classList.add("btn", "btn-outline-warning");
    upd.appendChild(updButton);
    updButton.innerText = "Update";
    updButton.onclick = function () {
        location.href = updatePageUrl + team["id"];
    };

    let delButton = document.createElement("button");
    delButton.classList.add("btn", "btn-outline-danger");
    del.appendChild(delButton);
    delButton.innerText = "Delete";
    delButton.addEventListener("click", function () {
        stomp.send(prefix + deletePageUrl + team["id"], {});
    });
}