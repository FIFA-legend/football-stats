const seasonsUrl = '/queue/seasons/return';

const prefix = '/websockets';
const firstPageUrl = '/seasons/start';
const nextPageUrl = '/seasons/next';
const previousPageUrl = '/seasons/previous';
const deletePageUrl = '/seasons/delete/';
const updatePageUrl = '/season/update/';

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe('/user' + seasonsUrl, processSeasons);
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

function processSeasons(income) {
    clearTableBody();
    let seasons = JSON.parse(income.body);
    let tbody = document.getElementsByTagName("tbody")[0];
    for (let i = 0; i < seasons.length; i++) {
        let tr = document.createElement("tr");
        addLineInTable(seasons[i], tr);
        tbody.appendChild(tr);
    }
}

let clearTableBody = function () {
    let tbody = document.getElementsByTagName("tbody")[0];
    while (tbody.lastChild) {
        tbody.removeChild(tbody.lastChild);
    }
}

let addLineInTable = function (season, tr) {
    let seasonName = document.createElement("td");
    let seasonStatus = document.createElement("td");
    let seasonStartDate = document.createElement("td");
    let seasonEndDate = document.createElement("td");
    let upd = document.createElement("td");
    let del = document.createElement("td");

    tr.appendChild(seasonName);
    tr.appendChild(seasonStatus);
    tr.appendChild(seasonStartDate);
    tr.appendChild(seasonEndDate);
    tr.appendChild(upd);
    tr.appendChild(del);

    seasonName.innerHTML = season["name"];
    if (season["isCurrent"]) {
        seasonStatus.innerHTML = "Current";
    } else {
        seasonStatus.innerHTML = "Finished";
    }
    seasonStartDate.innerHTML = season["startDate"];
    seasonEndDate.innerHTML = season["endDate"];

    let updButton = document.createElement("button");
    updButton.classList.add("btn", "btn-outline-warning");
    upd.appendChild(updButton);
    updButton.innerText = "Update";
    updButton.onclick = function () {
        location.href = updatePageUrl + season["id"];
    };

    let delButton = document.createElement("button");
    delButton.classList.add("btn", "btn-outline-danger");
    del.appendChild(delButton);
    delButton.innerText = "Delete";
    delButton.addEventListener("click", function () {
        stomp.send(prefix + deletePageUrl + season["id"], {});
    });
}
