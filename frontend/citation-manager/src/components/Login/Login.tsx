import React, { useState } from 'react';
import type { LoginProps } from '../../types/types.ts'
import './Login.css';
import { Link } from 'react-router-dom';

async function loginUser(credentials: { username: string; password: string; }) {
    return fetch('http://localhost:8090/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    });
}

/**
 * The log in UI component.
 */
const Login = ( { setTokenPair }: LoginProps ) => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    const doSubmit = async (e: React.ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();

        const res = await loginUser({ username, password });
        if (!res.ok) {
            setError("Incorrect username or password.")
            return;
        }

        const token = await res.json();
        setTokenPair(token);
    };

    return (
        <div className="login-wrapper">
            <h1>Welcome Back</h1>
            
            <form className='loginForm' onSubmit={doSubmit}>
                <label>                
                    <p>Username</p>
                    <input type="text" name="username" placeholder="" required onChange={e => setUsername(e.target.value)} />
                </label>
                <label>                
                    <p>Password</p>
                    <input type="password" name="password" placeholder="" required onChange={e => setPassword(e.target.value)} />
                </label>
                <p>
                    Dont have an account? {<Link to='/signup'>Sign up</Link>}
                </p>
                {error && <span className='error'>{error}</span>}
                <div className='submit-wrapper'>
                    <button type="submit">Sign In</button>
                </div>
            </form>
        </div>
    );
}

export default Login;