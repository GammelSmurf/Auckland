import {Form} from "react-bootstrap";
import React from "react";


const AucForm = (props) => {

        const parseMinDate = () => {
            return props.minDateTime.split(' ').join('T');
        }
        return (
                <div style={{minHeight: "400px"}}>
                    <Form.Group>
                        <Form.Label>
                            Name
                        </Form.Label>
                        <Form.Control required onChange={props.handleChange("aucName")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Description
                        </Form.Label>
                        <Form.Control as="textarea" rows={4} required onChange={props.handleChange("aucDescription")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Member limit
                        </Form.Label>
                        <Form.Control required type="number" min="1" max="100" defaultValue="100" onChange={props.handleChange("usersLimit")}/>
                        <Form.Control.Feedback type="invalid">
                            The value must be between 1 and 100
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Start
                        </Form.Label>
                        <Form.Control required type="datetime-local" step="1" min={parseMinDate()}
                                      defaultValue={parseMinDate()} onChange={props.handleChange("beginDate")}/>
                        <Form.Control.Feedback type="invalid">
                            {"The datetime must be after " + props.minDateTime}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Lot duration
                        </Form.Label>
                        <Form.Control required type="time" step="1" defaultValue={"00:30:00"} onChange={props.handleChange("lotDuration")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Label>
                            Lot boost
                        </Form.Label>
                        <Form.Control required type="time" step="1" defaultValue={"00:00:10"} onChange={props.handleChange("boostTime")}/>
                        <Form.Control.Feedback type="invalid">
                            The value cannot be empty
                        </Form.Control.Feedback>
                    </Form.Group>
                </div>
        )
}
export default AucForm;