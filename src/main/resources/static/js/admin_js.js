const usersUrl = '/queue/admin/users/return';

const prefix = '/websockets';
const firstPageUrl = '/admin/users';
const nextPageUrl = '/admin/users/next';
const previousPageUrl = '/admin/users/previous';
const deletePageUrl = '/admin/users/delete/';
const updatePageUrl = '/admin/users/update/';

let url = '/secured/room';
let sock = new SockJS(url);
let stomp = Stomp.over(sock);
stomp.connect({}, function () {
    stomp.subscribe('/user' + usersUrl, processUsers);
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

function processUsers(income) {
    clearTableBody();
    let users = JSON.parse(income.body);
    let tbody = document.getElementsByTagName("tbody")[0];
    for (let i = 0; i < users.length; i++) {
        let tr = document.createElement("tr");
        addLineInTable(users[i], tr);
        tbody.appendChild(tr);
    }
}

let clearTableBody = function () {
    let tbody = document.getElementsByTagName("tbody")[0];
    while (tbody.lastChild) {
        tbody.removeChild(tbody.lastChild);
    }
}

let addLineInTable = function (user, tr) {
    let username = document.createElement("td");
    let email = document.createElement("td");
    let role = document.createElement("td");
    let del = document.createElement("td");

    tr.appendChild(username);
    tr.appendChild(email);
    role.appendChild(createRole(user));
    tr.appendChild(role);
    tr.appendChild(del);

    username.innerHTML = user["username"];
    email.innerHTML = user["email"];

    let button = document.createElement("button");
    button.classList.add("btn", "btn-outline-danger");
    del.appendChild(button);
    button.innerText = "Delete";
    button.addEventListener("click", function () {
        stomp.send(prefix + deletePageUrl + user["id"], {});
    })
}

let createRole = function (user) {
    let role = user["role"];
    let select = document.createElement("select");
    select.classList.add("form-select", "form-select-sm");
    select.addEventListener("change", function () {
        user["role"] = select.children.item(select.selectedIndex).innerHTML;
        stomp.send(prefix + updatePageUrl + user["id"], {}, JSON.stringify(user));
    });

    let selectedOption = document.createElement("option");
    let option1 = document.createElement("option");

    selectedOption.innerHTML = role;
    selectedOption.setAttribute("selected", "selected");
    select.appendChild(selectedOption);
    select.appendChild(option1);

    if (role === "ADMIN") {
        option1.innerHTML = "USER";
    } else if (role === "USER") {
        option1.innerHTML = "ADMIN";
    }
    return select;
}