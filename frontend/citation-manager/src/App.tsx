import useTokenPair from './hooks/useTokenPair'
import Login from './components/Login/Login';
import './App.css'

const App = () => {
    const { tokenPair, setTokenPair, isAuthenticated, loading, clear } = useTokenPair();

    if(!isAuthenticated) {
        return <Login setTokenPair={setTokenPair} />
    }

    return (
        <div className="app-wrapper">
            <h1>Authenticated!</h1>
        </div>
    );
};

export default App
