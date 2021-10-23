import React from 'react';
import {Route, Switch, withRouter, Redirect} from 'react-router-dom';
import Home from './components/Home';
import Login from "./components/Login";
import NavBar from "./components/NavBar";
import AuthService from "./services/AuthService";
import GenericNotFound from "./components/GenericNotFound";
import Auction from "./components/Auction";

import './App.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';

import "./css/App.css"

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
          {(location.pathname === '/home' || location.pathname === '/auction') && <NavBar />}
        <div>
          <Switch>
              <PrivateRoute path='/home' exact={true} component={Home}/>
              <Route path='/auth/signin' exact={true} component={Login}/>
              <Route path='/auction' exact={true} component={Auction}/>
              <Route component={GenericNotFound} />
          </Switch>
        </div>
      </div>
  );
})

export default App;
