<jsp:include page="top.jsp"/>
    <!-- ko if: !selected.artist() -->
    <div class="form-inline">
        <div class="center query">
            <div class="form-group form-group-lg">
                <label class="sr-only" for="country">Country</label>
                <input data-bind="value: query.country" type="text" class="form-control" id="country" placeholder="Country...">
            </div>
            <button data-bind="click: function(){ $root.query.page(1); $root.fetchArtists(); }" type="submit" class="btn btn-primary btn-lg">Submit</button>
        </div>
    </div>
    <!-- /ko -->

    <!-- ko if: result.artists().length > 0 && !selected.artist() -->
    <div class="table-responsive">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>Image</th>
                <th>Name</th>
            </tr>
            </thead>
            <tbody data-bind="foreach: result.artists">
            <tr>
                <td data-bind="text: ($index() + 1) + (($root.query.page() - 1) * 5)"></td>
                <td>
                    <img data-bind="attr: { src: image[1]['#text']},
                                    click: function(){ $root.fetchTopTracks($data); }" class="img-rounded">
                </td>
                <td data-bind="text: name"></td>
            </tr>
            </tbody>
        </table>
    </div>

    <div class="center pages">
        <button data-bind="visible: $root.query.page() > 1,
                           click: function(){$root.query.page($root.query.page() - 1)}" class="btn btn-primary btn-lg"><<</button>
        <button data-bind="text: $root.query.page()" class="btn btn-default disabled btn-lg"></button>
        <button data-bind="visible: $root.query.page() < $root.result.totalPages(),
                           click: function(){$root.query.page($root.query.page() + 1)}" class="btn btn-primary btn-lg">>></button>
    </div>
    <!-- /ko -->

    <!-- ko if: selected.artist -->
        <div class="center">
            <h1 data-bind="text: selected.artist().name"></h1>
            <img data-bind="attr: { src: selected.artist().image[2]['#text']}" class="img-rounded">
        </div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
            </tr>
            </thead>
            <tbody data-bind="foreach: selected.topTracks">
            <tr>
                <td data-bind="text: ($index() + 1)"></td>
                <td data-bind="text: name"></td>
            </tr>
            </tbody>
        </table>
        <button data-bind="click: function(){ $root.selected.artist(null); }" class="btn btn-primary center"><< Back</button>
    <!-- /ko -->

    <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/knockout/3.4.1/knockout-min.js"></script>
    <script>
        $(function(){
            var vm = {};

            vm.query = {
                country: ko.observable(''),
                page: ko.observable(1),
                limit: 5
            };

            vm.result = {
                artists: ko.observableArray(),
                totalPages:  ko.observable()
            }

            vm.selected = {
                artist: ko.observable(),
                topTracks: ko.observableArray()
            }

            vm.fetchArtists = function(){
                $.post('/artists', {country: vm.query.country(), page: vm.query.page()}, function(data){
                    vm.result.artists([]);

                    if (data.error && data.message) {
                        alert("Error: " + data.message);
                        return;
                    }

                    if (data.topartists == null) {
                        return;
                    }

                    var artists = data.topartists.artist;

                    //radio.fm api sometimes will not use the limit and return more values
                    while(artists.length > vm.query.limit) {
                        artists.shift();
                    }

                    for(var i = 0; i < artists.length; i++) {
                        vm.result.artists.push(artists[i]);
                    }

                    var totalPages = parseInt(data.topartists['@attr'].totalPages);

                    vm.result.totalPages(totalPages);
                });
            };

            vm.fetchTopTracks = function(artist){
                vm.selected.topTracks([]);
                vm.selected.artist(artist);

                $.post('/toptracks', {name: artist.name}, function(data){
                    if (data.toptracks == null || data.toptracks.track == null) {
                        return;
                    }

                    var tracks = data.toptracks.track;

                    for(var i = 0; i < tracks.length; i++) {
                        vm.selected.topTracks.push(tracks[i]);
                    }
                });
            };

            vm.query.page.subscribe(function(){
                vm.fetchArtists();
            });

            ko.applyBindings(vm, $('#main-container').get(0));
        });
    </script>
    <style>
        #main-container {
            font-size: 2em;
        }
        .center {
            margin: auto;
            padding: 10px;
            display: block;
            text-align: center;
        }
        .query {
            width: 370px;
        }
        .pages {
            width: 200px;
        }
        .table {
            max-width: 760px;
            margin: auto;
        }
        .table td{
            vertical-align: middle !important;
        }
        .table td img {
            cursor: pointer;
        }
        #country {
            margin-bottom: 10px;
        }
    </style>
<jsp:include page="bottom.jsp"/>
