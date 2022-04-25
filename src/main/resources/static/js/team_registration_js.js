setTimeout(function () {
    let nameErrorTag = document.getElementById("name_errors");
    let stadiumErrorTag = document.getElementById("stadium_errors");
    let countryErrorTag = document.getElementById("country_errors");

    let nameTag = document.getElementById("name");
    let stadiumTag = document.getElementById("homeStadium");
    let countryTag = document.getElementById("country");

    let flag1 = checkNotEmptyTag(nameTag, nameErrorTag);
    let flag2 = checkNotEmptyTag(stadiumTag, stadiumErrorTag);
    let flag3 = checkNotEmptyTag(countryTag, countryErrorTag);

    let button = document.getElementById("button");
    showButton(flag1, flag2, flag3, button);

    nameTag.addEventListener("keyup", function () {
        flag1 = checkNotEmptyTag(nameTag, nameErrorTag);
        showButton(flag1, flag2, flag3, button);
    });

    stadiumTag.addEventListener("keyup", function () {
        flag2 = checkNotEmptyTag(stadiumTag, stadiumErrorTag);
        showButton(flag1, flag2, flag3, button);
    });

    countryTag.addEventListener("keyup", function () {
        flag3 = checkNotEmptyTag(countryTag, countryErrorTag);
        showButton(flag1, flag2, flag3, button);
    });


}, 500);

let checkNotEmptyTag = function (input, error) {
    if (input.value.length <= 0) {
        error.innerHTML = "Not Empty";
        return false;
    } else {
        error.innerHTML = "";
        return true;
    }
}

let showButton = function (flag1, flag2, flag3, button) {
    button.disabled = !(flag1 && flag2 && flag3);
}