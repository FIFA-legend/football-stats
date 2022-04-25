const leaguesUrl = '/queue/leagues/return';

const prefix = '/websockets';
const firstPageUrl = '/leagues/start';
const nextPageUrl = '/leagues/next';
const previousPageUrl = '/leagues/previous';
const deletePageUrl = '/leagues/delete/';
const updatePageUrl = '/league/update/';

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe('/user' + leaguesUrl, processLeagues);
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

function processLeagues(income) {
    clearTableBody();
    let leagues = JSON.parse(income.body);
    let tbody = document.getElementsByTagName("tbody")[0];
    for (let i = 0; i < leagues.length; i++) {
        let tr = document.createElement("tr");
        addLineInTable(leagues[i], tr);
        tbody.appendChild(tr);
    }
}

let clearTableBody = function () {
    let tbody = document.getElementsByTagName("tbody")[0];
    while (tbody.lastChild) {
        tbody.removeChild(tbody.lastChild);
    }
}

let addLineInTable = function (league, tr) {
    let leagueName = document.createElement("td");
    let countryName = document.createElement("td");
    let upd = document.createElement("td");
    let del = document.createElement("td");

    tr.appendChild(leagueName);
    tr.appendChild(countryName);
    tr.appendChild(upd);
    tr.appendChild(del);

    leagueName.innerHTML = league["name"];
    countryName.innerHTML = league["country"]["name"];

    let updButton = document.createElement("button");
    updButton.classList.add("btn", "btn-outline-warning");
    upd.appendChild(updButton);
    updButton.innerText = "Update";
    updButton.onclick = function () {
        location.href = updatePageUrl + league["id"];
    };

    let delButton = document.createElement("button");
    delButton.classList.add("btn", "btn-outline-danger");
    del.appendChild(delButton);
    delButton.innerText = "Delete";
    delButton.addEventListener("click", function () {
        stomp.send(prefix + deletePageUrl + league["id"], {});
    });
}
