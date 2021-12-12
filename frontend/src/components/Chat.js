import React, {useEffect, useRef, useState} from 'react';
import 'react-chat-widget/lib/styles.css';
import {Button, FormControl, InputGroup} from "react-bootstrap";
import {faPaperPlane} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const Chat = (props) => {
    const [message, setMessage] = useState('');
    const messageInput = useRef(null);

    useEffect(() => {

    }, []);

    const onChangeMessage = (e) => {
        setMessage(e.target.value);
    }

    const handleSendMessage = () => {
        props.handleChatMessage(message);
        messageInput.current.value = '';
    }

    const handleSendMessageByEnter = (e) => {
        if(e.key === 'Enter'){
            e.preventDefault();
            handleSendMessage();
        }
    }

    return(
        <div>
            <div id="messageList" style={{overflowY: 'auto', height: '470px'}}>
                {props.messages.length !==0 ? props.messages.map(message =>
                    <div key={message.id}>
                        <div className='messageBox' style={{marginBottom: '10px'}}>
                            <div>
                            <span>
                                <b>{message.username}</b>
                            </span>
                            </div>
                            <p style={{margin: 0}}>{message.message}
                                <span className='messageBoxTime'>{message.dateTime}</span>
                            </p>
                        </div>

                    </div>
                ) : <p style={{fontStyle: 'italic'}}>There is no messages yet</p>}
            </div>

            <div style={{padding: '10px 0px'}}>
                <div style={{display: 'inline-block', width: '80%'}}>
                    <InputGroup>
                        <FormControl aria-label="Amount (to the nearest dollar)"
                                     ref={messageInput}
                                     onChange={onChangeMessage}
                                     onKeyDown={handleSendMessageByEnter}
                                     placeholder="Message" type='text' style={{borderRadius: '7px'}}/>
                    </InputGroup>
                </div>
                <div style={{width: '20%', display: 'inline-block'}}>
                    <Button variant="warning" onClick={handleSendMessage}
                            style={{borderRadius: '50%', marginLeft: '10px'}}>
                        <FontAwesomeIcon icon={faPaperPlane} color="white" size="sm"
                                         style={{cursor: "hand"}}/>
                    </Button>
                </div>

            </div>

        </div>
    )
}

export default Chat;