import SockJS from 'sockjs-client'
import {Stomp} from '@stomp/stompjs'
import React from 'react';

let stompClient = null
const LoggerHandlers = []
const BetHandlers = []

const connect = (id) => {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, (frame) => {
        //console.log('Connected: ' + frame);
        stompClient.subscribe('/auction/logs/' + id, (response) => {
            LoggerHandlers.forEach(handler => handler(JSON.parse(response.body)));
        });
        stompClient.subscribe('/auction/state/' + id, (response) => {
            console.log('Bet WS Response')
            console.log(response)
            BetHandlers.forEach(handler => handler(JSON.parse(response.body)));
        });
    });
    return true;
}

const addLoggerHandler = (handler) => {
    LoggerHandlers.push(handler);
}

const addBetHandler = (handler) => {
    BetHandlers.push(handler);
}

const sendBet = (id, username, bet) => {
    stompClient.send('/app/play/' + id, {}, JSON.stringify({username: username, currentBank: bet}));
}

export default {connect, addLoggerHandler, addBetHandler, sendBet}