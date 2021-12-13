import {Form, FormControl, InputGroup} from "react-bootstrap";
import React from "react";


const EditAucForm = (props) => {
    return (
        <div className="configBlock">
            <Form.Group>
                <FormControl required type="datetime-local" step="1" defaultValue={props.auction.beginDateTime} min={props.minDate} onChange={props.handleChange("beginDateTime")}/>
                <Form.Control.Feedback type="invalid">
                    Auction's begin date must be in the future
                </Form.Control.Feedback>
            </Form.Group>
            <div className="mt-3 mb-5">
                <h5>Settings</h5>
                <Form.Group>
                    <InputGroup>
                        <InputGroup.Text>Members count limit</InputGroup.Text>
                        <Form.Control required type="number" min="1" max="100" defaultValue={props.auction.usersCountLimit} onChange={props.handleChange("usersCountLimit")}/>
                    </InputGroup>
                    <Form.Control.Feedback type="invalid">
                        The value must be between 1 and 100
                    </Form.Control.Feedback>
                </Form.Group>
                <Form.Group>
                    <InputGroup>
                        <InputGroup.Text>Lot duration time</InputGroup.Text>
                        <Form.Control required type="time" step="1" defaultValue={props.auction.lotDurationTime} onChange={props.handleChange("lotDurationTime")}/>
                    </InputGroup>
                    <Form.Control.Feedback type="invalid">
                        The value cannot be empty
                    </Form.Control.Feedback>
                </Form.Group>
                <Form.Group>
                    <InputGroup>
                        <InputGroup.Text>Lot extra time</InputGroup.Text>
                        <Form.Control required type="time" step="1" defaultValue={props.auction.extraTime} onChange={props.handleChange("extraTime")}/>
                    </InputGroup>
                    <Form.Control.Feedback type="invalid">
                        The value cannot be empty
                    </Form.Control.Feedback>
                </Form.Group>
            </div>
        </div>
    )
}
export default EditAucForm;