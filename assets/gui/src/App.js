import React, {Component} from 'react'
import FurhatGUI from 'furhat-gui'
import { Grid, Row, Col } from 'react-bootstrap'
import Button from './Button'
import Input from './Input'

class App extends Component {

    constructor(props) {
        super(props)
        this.state = {
          "speaking": false,
          "buttons": [],
          "inputFields": []
        }
        this.furhat = null
    }

    setupSubscriptions() {
        // Our DataDelivery event is getting no custom name and hence gets it's full class name as event name.
        this.furhat.subscribe('furhatos.app.quizskill.DataDelivery', (data) => {
            this.setState({
                ...this.state,
                buttons: data.buttons,
                inputFields: data.inputFields
            })
        })

        // This event contains to data so we defined it inline in the flow
        this.furhat.subscribe('SpeechDone', () => {
            this.setState({
                ...this.state,
                speaking: false
            })
        })
    }

    componentDidMount() {
        FurhatGUI()
            .then(connection => {
                this.furhat = connection
                this.setupSubscriptions()
            })
            .catch(console.error)
    }

    clickButton = (button) => {
        this.setState({
            ...this.state,
            speaking: true
        })
        this.furhat.send({
          event_name: "ClickButton",
          data: button
        })
    }

    variableSet = (variable, value) => {
        this.setState({
            ...this.state,
            speaking: true
        })
        this.furhat.send({
          event_name: "VariableSet",
          data: {
            variable,
            value
          }
        })
    }

    render() {
      return (
          <Grid>
            <Row>
                <Col sm={12}>
                    <h1>Mülltrennungsspiel</h1>
                </Col>
            </Row>
            <Row>
                <Col sm={6}>
                    { this.state.buttons.map((label) =>
                        <Button key={label} label={label} onClick={this.clickButton} speaking={this.state.speaking}/>
                    )}
                </Col>
            </Row>
          </Grid>
        )
    }

}

export default App;
