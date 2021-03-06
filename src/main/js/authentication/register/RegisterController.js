import User from '../../user/User';
import Authority from '../../user/authority/Authority';
export default class RegisterController {

    /*@ngInject*/
    constructor($scope, $rootScope, userService, navigationService) {

        this.$rootScope = $rootScope;
        this.userService = userService;
        this.navigationService = navigationService;

        this.$rootScope.title = 'Registrieren';
        this.$rootScope.loading = false;

        this.user = new User({authorities: [new Authority(Authority.USER_ROLE)]});

        this._initDestroyListener($scope);
    }

    register() {
        this.$rootScope.loading = true;
        this.userService.createUser(this.user)
            .then(() => {
                this.navigationService.goto('/register/confirmation');
            })
            .catch(() => {
                this.$rootScope.loading = false;
            });
    }

    submitIsDisabled() {
        return !this.registerForm.$valid;
    }

    _initDestroyListener($scope) {
        $scope.$on('$destroy', () => {

            this.$rootScope.reset();
        });
    }
}