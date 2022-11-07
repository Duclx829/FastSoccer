var sortPriceDropdown = $('#dropdown-sortprice');
var districtDropdown = $('#dropdown-district');


(function () {
    if (sortPriceDropdown.attr("order-data") == undefined || sortPriceDropdown.attr("order-data") == "") {
        sortPriceDropdown.val("Giá:");
    } else {
        insertSortField(sortPriceDropdown.attr("order-data"));
        if (sortPriceDropdown.attr("order-data") == "asc") {
            sortPriceDropdown.val("Giá: Thấp đến cao");
        } else {
            sortPriceDropdown.val("Giá: Cao đến thấp");
        }
    }
})();
(function () {
    if (districtDropdown.attr("district-id-data") == undefined || districtDropdown.attr("district-id-data") == "") {
        districtDropdown.val("Quận/Huyện");
    } else {
        insertDistrictField(districtDropdown.attr("district-id-data"));
    }
})();

function insertSortField(value) {
    if ($('.dropdown #sortprice').length == 0) {
        sortPriceDropdown.parent().prepend(`
            <input hidden name="sortby" value="price">
            <input id="sortprice" hidden name="order"">
         `);
    }
    sortPriceDropdown.parent().find("#sortprice").val(value);
}

function insertDistrictField(id) {
    if ($('.dropdown #district').length == 0) {
        districtDropdown.parent().prepend(`<input id="district" hidden name="district">`);
    }
    districtDropdown.parent().find("#district").val(id);
}

sortPriceDropdown.parent().find('li').each(function (ind, cont) {
    $(cont).find('a').first().click(function () {
        sortPriceDropdown.val(this.text);
        insertSortField(this.getAttribute("order-data"));
    });
});

districtDropdown.parent().find('li').each(function (ind, cont) {
    $(cont).find('a').first().click(function () {
        districtDropdown.val(this.text);
        insertDistrictField(this.getAttribute("distric-id"));
    });
});


