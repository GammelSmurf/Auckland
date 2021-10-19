import React from 'react';
import {Route, Switch, withRouter, Redirect} from 'react-router-dom';
import './App.css';
import Home from './components/Home';
import Login from "./components/Login";
import NavBar from "./components/NavBar";
import AuthService from "./services/AuthService";
import 'bootstrap/dist/css/bootstrap.min.css';
import GenericNotFound from "./components/GenericNotFound";

const App = withRouter(({location})=> {

    const PrivateRoute = ({ component: Component, ...rest }) => (
        <Route {...rest} render={props => (
            AuthService.isAuthenticated() ? (
                <Component {...props}/>
            ) : (
                <Redirect to={{
                    pathname: '/auth/signin',
                    state: { from: props.location }
                }}/>
            )
        )}/>
    )
  return (
      <div>
          {location.pathname === '/home' && <NavBar />}
        <div>
          <Switch>
              <PrivateRoute path='/home' exact={true} component={Home}/>
              <Route path='/auth/signin' exact={true} component={Login}/>
              <Route component={GenericNotFound} />
          </Switch>
        </div>
      </div>
  );
})

export default App;
