let shList = document.getElementById("sh-list");
let smList = document.getElementById("sm-list");
let ehList = document.getElementById("eh-list");
let emList = document.getElementById("em-list");
let listHourStart = shList.getElementsByTagName("li");
let listMinStart = smList.getElementsByTagName("li");
let listHourEnd = ehList.getElementsByTagName("li");
let listMinEnd = emList.getElementsByTagName("li");
document.addEventListener('click', (e) => {
    if (e.target.id != 'sHour') {
        shList.classList.remove("active");
    } else {
        shList.classList.add("active");
    }
    if (e.target.id != 'sMin') {
        smList.classList.remove("active");
    } else {
        smList.classList.add("active");
    }
    if (e.target.id != 'eHour') {
        ehList.classList.remove("active");
    } else {
        ehList.classList.add("active");
    }
    if (e.target.id != 'eMin') {
        emList.classList.remove("active");
    } else {
        emList.classList.add("active");
    }
});

function setTimeValue(id, hId, mId) {
    document.getElementById(id).value =
        document.getElementById(hId).value +
        ":" +
        document.getElementById(mId).value
}
(function() {
    for (let i = 0; i < listHourStart.length; ++i) {
        listHourStart[i].addEventListener('click', (e) => {
            document.getElementById("sHour").value = e.target.textContent
            setTimeValue('stime','sHour','sMin');
            shList.classList.remove("active");

        })
    }
    for (let i = 0; i < listMinStart.length; ++i) {
        listMinStart[i].addEventListener('click', (e) => {
            document.getElementById("sMin").value = e.target.textContent
            setTimeValue('stime','sHour','sMin');
            smList.classList.remove("active");
        })
    }
    for (let i = 0; i < listHourEnd.length; ++i) {
        listHourEnd[i].addEventListener('click', (e) => {
            document.getElementById("eHour").value = e.target.textContent
            setTimeValue('etime','eHour','eMin');
            ehList.classList.remove("active");

        })
    }
    for (let i = 0; i < listMinEnd.length; ++i) {
        listMinEnd[i].addEventListener('click', (e) => {
            document.getElementById("eMin").value = e.target.textContent
            setTimeValue('etime','eHour','eMin');
            emList.classList.remove("active");
        })
    }
}());
