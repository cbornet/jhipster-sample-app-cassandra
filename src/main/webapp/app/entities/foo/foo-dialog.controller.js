(function() {
    'use strict';

    angular
        .module('jhipsterCassandraSampleApplicationApp')
        .controller('FooDialogController', FooDialogController);

    FooDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Foo'];

    function FooDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Foo) {
        var vm = this;

        vm.foo = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.foo.id !== null) {
                Foo.update(vm.foo, onSaveSuccess, onSaveError);
            } else {
                Foo.save(vm.foo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('jhipsterCassandraSampleApplicationApp:fooUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setBar = function ($file, foo) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        foo.bar = base64Data;
                        foo.barContentType = $file.type;
                    });
                });
            }
        };

    }
})();
