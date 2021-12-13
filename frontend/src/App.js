import React, {useState} from 'react';
import {Route, Switch, withRouter, Redirect,matchPath} from 'react-router-dom';
import Home from './components/Home';
import Login from "./components/Login";
import NavBar from "./components/NavBar";
import AuthService from "./services/AuthService";
import GenericNotFound from "./components/GenericNotFound";
import Auctions from "./components/Auctions";

import './css/App.css';
import './css/auctions.css';
import './css/auction.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import Register from "./components/Register";
import Auction from "./components/Auction";
import Users from "./components/Users";

const App = withRouter((props)=> {
    const isAuthenticated = AuthService.isAuthenticated();

    const PrivateRoute = ({ component: Component, ...rest }) => (
        <Route {...rest} render={props => (
            isAuthenticated ? (
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

          {(isAuthenticated && (props.location.pathname === '/home' || props.location.pathname === '/auctions' || matchPath(props.location.pathname, { path: '/auctions/:id' }) || props.location.pathname === '/users')) && <NavBar history={props.history}/>}
        <div>
          <Switch>
              <PrivateRoute path='/home' exact={true} component={Home}/>
              <Route path='/auth/signin' exact={true} component={Login}/>
              <Route path='/auth/signup' exact={true} component={Register}/>
              <PrivateRoute path='/auctions' exact={true} component={Auctions}/>
              <PrivateRoute path='/auctions/:id' exact={true} component={Auction}/>
              <PrivateRoute path='/users' exact={true} component={Users}/>
              <Route component={GenericNotFound} />
          </Switch>
        </div>
      </div>
  );
})

export default App;
