
function Search(formId, queryInputId) {
	this.query = "";
	this.offset = 0;
	
	this.container = "#container";
	this.moreId = '#more';
	this.formId = formId;
	
	var THIS = this;
	$(formId).submit(function(event) {
		//debugger;
		THIS.setQuery($(queryInputId).val());
		THIS.fetchNext(10);
		return false;
	});
	
	$(this.moreId).click(function(event) {
		THIS.fetchNext(10);
	});
}

Search.prototype.setQuery = function(query) {
	this.query = query;
}

Search.prototype.fetchNext = function(number) {
	var THIS = this;
    $.ajax({
        type: "GET",
        url: "search",
        data: { query: this.query, offset: this.offset, number: number },
        success: function(data, textStatus, jqxhr){/*alert(JSON.stringify(data)) */;THIS.populate(eval(data));},
        error: function(ajax, text, error) {alert(error)}
    });

    this.offset = this.offset + number;
}

Search.prototype.populate = function(jsonResult) {
	//alert(jsonResult.length);
    if( jsonResult === undefined ) {
        $(this.container).hide();
    } else {
        //for( i = 0 ; i < jsonResult.length ; i++ ) {
    	for(var i in jsonResult) {
        	var data = jsonResult[i];
        	//alert(i + ":" + data);
            $('<div class="result" id="node' + (i+this.offset) + '" style="text-align:center">' + data.document.avatar + '</div>').hide().insertAfter(this.container).fadeIn(600);
            //getNode(data[i].document._id, "node" + (i+offset));
        }

        if( data.length < 10 ) {
            $(this.moreId).hide();
        }
    }
}