import React from 'react';
import {faCrown} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

const Members = (props) => {

    return(
        <div>
            <div>
                <h5><FontAwesomeIcon icon={faCrown} color="#FFCA2C" size="sm"/> {props.owner && props.owner.username} </h5>
            </div>
            {props.members && props.members.map(user=>
                <div key={user.id}>
                    <h5>{`${props.members.indexOf(user) + 1}. ${user.username}`}</h5>
                </div>
            )}
        </div>
    )
}

export default Members;