(function() {
    'use strict';

    angular
        .module('jhipsterCassandraSampleApplicationApp')
        .controller('FooController', FooController);

    FooController.$inject = ['DataUtils', 'Foo'];

    function FooController(DataUtils, Foo) {

        var vm = this;

        vm.foos = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        loadAll();

        function loadAll() {
            Foo.query(function(result) {
                vm.foos = result;
                vm.searchQuery = null;
            });
        }
    }
})();
