import { Component } from 'react'

export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false }
  }

  static getDerivedStateFromError() {
    return { hasError: true }
  }

  componentDidCatch(error, info) {
    console.error('ErrorBoundary caught:', error, info)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary-fallback">
          <p>Something went wrong displaying this content.</p>
          <button
            type="button"
            onClick={() => this.setState({ hasError: false })}
          >
            Try again
          </button>
        </div>
      )
    }
    return this.props.children
  }
}
