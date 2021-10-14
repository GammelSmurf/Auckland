import React from 'react';
import {Route, Switch} from 'react-router-dom';
import './App.css';
import Home from './Components/Home'


const App = () =>{
  return (
      <div>
        <div>
          <Switch>
              <Route path='/home' exact={true} component={Home}/>
          </Switch>
        </div>
      </div>

  );
}

export default App;
