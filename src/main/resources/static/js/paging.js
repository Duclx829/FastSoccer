// var txtSearch = $('#search-history').val();
//
// if ($('#search-history').attr('search-data') == 'null') {
//     txtSearch = undefined;
// }
//href="${pagingIndex == 1 ? '' : destinationUrl+ txtSearch + 'page=' + (pagingIndex - 1)}"
var href = window.location.href;
var urlMap = "";
var pagingIndex = 1;
var itemsPerPage = 5;
var pageParam = new URLSearchParams(window.location.search);
if (pageParam.has('page')) {
    pagingIndex = pageParam.get('page');
}

function loadPagination(mapping) {
    urlMap = mapping;
    var maxPage = $('#maxPage').val();
    var count = -3;
    var maxIndex = (pagingIndex <= 4 || pagingIndex >= maxPage - 3) && maxPage > 9 ? 7 : 9;
    const pagingList = $('#pagination');
    maxIndex = maxPage <= 8 ? maxPage : maxIndex;

    if (maxPage > 1) {
        $('#pagingation-container').css("display", "block");
        $('#search-form').prepend('<input hidden name="page" value="1">');
    } else {
        return;
    }
    for (let i = 0; i < maxIndex; i++) {
        if (i == 0 || i == maxIndex - 1) {
            if (i == 0 && pagingIndex == 1) {
                pagingList.append(`<li><a class="active" onclick="pagingHandle(1)">1</a></li>`);
            } else if (i == maxIndex - 1 && pagingIndex == maxPage) {
                pagingList.append(`<li><a class="page-link active" onclick="pagingHandle(${maxPage})">${maxPage}</a></li>`);
            } else {
                pagingList.append(`<li><a onclick="pagingHandle(${i == 0 ? 1 : maxPage})">${i == 0 ? 1 : maxPage}</a></li>`);
            }
        } else {
            if (maxPage <= 9) {
                pagingList.append(`<li><a class="${i + 1 == pagingIndex ? "active" : ""}" onclick="pagingHandle(${i + 1})">${i + 1}</a></li>`);
            } else if (pagingIndex < 5) {
                pagingList.append(`<li><a class="${pagingIndex == (i + 1) ? "active" : i == maxIndex - 2 ? "disable text-dark" : ""}" 
                                            onclick="pagingHandle(${i + 1})">${i == maxIndex - 2 ? "..." : (i + 1)}
                                       </a>
                                   </li>`);
            } else {
                let tempPagingIndex = parseInt(pagingIndex) + count++;
                if (pagingIndex >= maxPage - 3) {
                    if (pagingIndex == maxPage - 3) {
                        pagingList.append
                        (`<li>
                           <a class="${pagingIndex == tempPagingIndex + 1 ? "active" : i == 1 ? "disable text-dark" : ""}" 
                              onclick="pagingHandle(${tempPagingIndex + 1})">
                              ${i == 1 ? "..." : tempPagingIndex + 1}
                           </a>
                        </li>`);
                    } else {
                        pagingList.append
                        (`<li>
                           <a class="${pagingIndex == (maxPage - 6 + i) ? "active" : i == 1 ? "disable text-dark" : ""}"
                                onclick="pagingHandle(${maxPage - 6 + i})">
                               ${i == 1 ? "..." : maxPage - 6 + i}</a>
                        </li>`);
                    }
                } else {
                    pagingList.append
                    (`<li>
                       <a class="${pagingIndex == tempPagingIndex ? "active" : (i == maxIndex - 2) || (i == 1) ? "disable text-dark" : ""}" 
                            onclick="pagingHandle(${tempPagingIndex})">
                            ${(i == maxIndex - 2) || (i == 1) ? "..." : tempPagingIndex}
                       </a>
                    </li>`);
                }
            }
        }
    }
    //append previous and next button
    pagingList.prepend(`<li><a class="px-4 ${pagingIndex == 1 ? "disable" : ""}" onclick="pagingHandle(--pagingIndex)">«</a></li>`);
    pagingList.append(`<li><a class="px-4 ${pagingIndex == maxPage ? "disable" : ""}" onclick="pagingHandle(++pagingIndex)">»</a></li>`);
}


function pagingHandle(index) {
    href = href.slice(href.indexOf(urlMap), href.length);
    if (href.charAt(urlMap.length) == '?') {
        if (pageParam.has('page')) {
            href = href.slice(urlMap.length + 7 + pagingIndex.toString().length, href.length);
        } else {
            href = href.slice(urlMap.length + 1, href.length);
        }
        if (href.length > 0 && href.charAt(0) != '&') {
            href = "&" + href;
        }
    } else {
        href = "";
    }
    window.location.href = urlMap + "?page=" + index + href;

}