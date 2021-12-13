import {Dropdown} from "react-bootstrap";
import React from "react";
import {faBell} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";


const Notifications = (props) => {

    const CustomToggle = React.forwardRef(({ children, onClick }, ref) => (
        <div ref={ref}
             onClick={(e) => {
                 e.preventDefault();
                 onClick(e);
             }}>
            <FontAwesomeIcon icon={faBell} color="#000" size="1x" style={{cursor: "hand", color: '#FFC107', fontSize: '20px'}}/>
            <span className='notificationCounter'>{props.counter}</span>
        </div>
    ));

    return(
        <Dropdown className='notificationDropdown' onToggle={(isOpen)=>!isOpen && props.reset()}>
            <Dropdown.Toggle as={CustomToggle} id="dropdown-basic">
            </Dropdown.Toggle>

            <Dropdown.Menu className='notificationBlock'>
                {props.notifications.length !== 0 ? props.notifications.map(notification =>
                    <Dropdown.Item key={notification.id} style={{padding: '5px 10px'}} onClick={() => props.history.push('/auctions/' + notification.auctionId)}>
                        {(props.newNotificationsIds && props.newNotificationsIds.includes(notification.id)) && <span style={{color: '#00AA8D'}}><b>NEW</b></span>}
                        <p>{notification.message}</p>
                        <hr/>
                    </Dropdown.Item>
                ).reverse() :
                <p style={{fontStyle: 'italic'}}>There is no notifications yet</p>}
            </Dropdown.Menu>
        </Dropdown>
    )
}

export default Notifications;