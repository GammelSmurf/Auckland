import React from 'react';
import {Route, Switch, withRouter, Redirect,matchPath} from 'react-router-dom';
import Home from './components/Home';
import Login from "./components/Login";
import NavBar from "./components/NavBar";
import AuthService from "./services/AuthService";
import GenericNotFound from "./components/GenericNotFound";
import Auctions from "./components/Auctions";

import './css/App.css';
import './css/auction.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-bootstrap-table-next/dist/react-bootstrap-table2.min.css';
import Register from "./components/Register";
import Auction from "./components/Auction";

const App = withRouter((props)=> {

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
          {(props.location.pathname === '/home' || props.location.pathname === '/auctions' || matchPath(props.location.pathname, { path: '/auctions/:id' })) && <NavBar />}
        <div>
          <Switch>
              <PrivateRoute path='/home' exact={true} component={Home}/>
              <Route path='/auth/signin' exact={true} component={Login}/>
              <Route path='/auth/signup' exact={true} component={Register}/>
              <Route path='/auctions' exact={true} component={Auctions}/>
              <Route path='/auctions/:id' exact={true} component={Auction}/>
              <Route component={GenericNotFound} />
          </Switch>
        </div>
      </div>
  );
})

export default App;
