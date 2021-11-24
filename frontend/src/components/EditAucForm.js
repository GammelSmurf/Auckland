import {Form, FormControl, InputGroup} from "react-bootstrap";
import React from "react";


const EditAucForm = (props) => {
    return (
        <div className="configBlock">
            <Form.Group>
                <FormControl required type="datetime-local" step="1" defaultValue={props.auction.beginDate} onChange={props.handleChange("beginDate")}/>
                <Form.Control.Feedback type="invalid">
                    The value cannot be empty
                </Form.Control.Feedback>
            </Form.Group>
            <div className="mt-3 mb-5">
                <h5>Settings</h5>
                <Form.Group>
                    <InputGroup>
                        <InputGroup.Text>Member limit</InputGroup.Text>
                        <Form.Control required type="number" min="1" max="100" defaultValue="100" onChange={props.handleChange("usersLimit")}/>
                    </InputGroup>
                    <Form.Control.Feedback type="invalid">
                        The value must be between 1 and 100
                    </Form.Control.Feedback>
                </Form.Group>
                <Form.Group>
                    <InputGroup>
                        <InputGroup.Text>Lot duration</InputGroup.Text>
                        <Form.Control required type="time" step="1" defaultValue={props.auction.lotDuration} onChange={props.handleChange("lotDuration")}/>
                    </InputGroup>
                    <Form.Control.Feedback type="invalid">
                        The value cannot be empty
                    </Form.Control.Feedback>
                </Form.Group>
                <Form.Group>
                    <InputGroup>
                        <InputGroup.Text>Lot boost time</InputGroup.Text>
                        <Form.Control required type="time" step="1" defaultValue={props.auction.boostTime} onChange={props.handleChange("boostTime")}/>
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