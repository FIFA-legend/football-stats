const venuesUrl = '/queue/venues/return';

const prefix = '/websockets';
const firstPageUrl = '/venues/start';
const nextPageUrl = '/venues/next';
const previousPageUrl = '/venues/previous';
const deletePageUrl = '/venues/delete/';
const updatePageUrl = '/venue/update/';

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe('/user' + venuesUrl, processVenues);
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

function processVenues(income) {
    clearTableBody();
    let venues = JSON.parse(income.body);
    let tbody = document.getElementsByTagName("tbody")[0];
    for (let i = 0; i < venues.length; i++) {
        let tr = document.createElement("tr");
        addLineInTable(venues[i], tr);
        tbody.appendChild(tr);
    }
}

let clearTableBody = function () {
    let tbody = document.getElementsByTagName("tbody")[0];
    while (tbody.lastChild) {
        tbody.removeChild(tbody.lastChild);
    }
}

let addLineInTable = function (venue, tr) {
    let venueName = document.createElement("td");
    let venueCountry = document.createElement("td");
    let venueCity = document.createElement("td");
    let venueCapacity = document.createElement("td");
    let upd = document.createElement("td");
    let del = document.createElement("td");

    tr.appendChild(venueName);
    tr.appendChild(venueCountry);
    tr.appendChild(venueCity);
    tr.appendChild(venueCapacity);
    tr.appendChild(upd);
    tr.appendChild(del);

    venueName.innerHTML = venue["name"];
    venueCountry.innerHTML = venue["country"]["name"];
    venueCity.innerHTML = venue["city"];
    venueCapacity.innerHTML = venue["capacity"];

    let updButton = document.createElement("button");
    updButton.classList.add("btn", "btn-outline-warning");
    upd.appendChild(updButton);
    updButton.innerText = "Update";
    updButton.onclick = function () {
        location.href = updatePageUrl + venue["id"];
    };

    let delButton = document.createElement("button");
    delButton.classList.add("btn", "btn-outline-danger");
    del.appendChild(delButton);
    delButton.innerText = "Delete";
    delButton.addEventListener("click", function () {
        stomp.send(prefix + deletePageUrl + venue["id"], {});
    });
}
