import React from 'react';
import {Route, Switch, withRouter, Redirect} from 'react-router-dom';
import Home from './components/Home';
import Login from "./components/Login";
import NavBar from "./components/NavBar";
import AuthService from "./services/AuthService";
import GenericNotFound from "./components/GenericNotFound";
import Auction from "./components/Auction";

import './css/App.css';
import './css/auction.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import Register from "./components/Register";

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
              <Route path='/auth/signup' exact={true} component={Register}/>
              <Route path='/auction' exact={true} component={Auction}/>
              <Route component={GenericNotFound} />
          </Switch>
        </div>
      </div>
  );
})

export default App;
