function showNotify(msg, type) {
    var notify = document.getElementById("notif");
    if (notify != undefined && notify != 'undefined') {
        notify.parentNode.removeChild(notify);
    }
    notify = document.createElement("div");
    notify.setAttribute("id", "notif");
    notify.textContent = msg;
    notify.classList.add(type);
    document.getElementById("header").prepend(notify);
    document.getElementById('notif').classList.toggle('visible');
}