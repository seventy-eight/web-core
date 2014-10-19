
function Search() {
	this.query = "";
	this.offset = 0;
}

Search.prototype.setQuery = function(query) {
	this.query = query;
}

Search.prototype.fetchNext = function(number) {
    $.ajax({
        type: "GET",
        url: "search",
        data: { query: this.query, offset: this.offset, number: number },
        success: function(data, textStatus, jqxhr){alert("YAY");},
        error: function(ajax, text, error) {alert(error)}
    });

    this.offset = this.offset + number;
}

Search.prototype.populate = function(jsonResult) {
    if( jsonResult === undefined ) {
        $('#bottom').hide();
    } else {
        //for( i = 0 ; i < jsonResult.length ; i++ ) {
    	for(var i in jsonResult) {
        	var data = jsonResult[i];
            $('<tr><td id="node' + (i+offset) + '" style="text-align:center">' + data.document.avatar + '</td></tr>').hide().insertBefore("#bottom").fadeIn(600);
            //getNode(data[i].document._id, "node" + (i+offset));
        }

        if( data.length < 10 ) {
            $('#bottom').hide();
        }
    }
}