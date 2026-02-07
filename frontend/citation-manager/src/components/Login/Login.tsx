import React, { useState } from 'react';
import type { LoginProps } from '../../types/types.ts'
import './Login.css';

async function loginUser(credentials: { username: string; password: string; }) {
    return fetch('http://localhost:8090/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(credentials)
    })
    .then(data => data.json());
}

const Login = ( { setTokenPair }: LoginProps ) => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const doSubmit = async (e: React.ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        const token = await loginUser({username, password});
        setTokenPair(token);
    };

    return (
        <div className="login-wrapper">
            <h1>Please log in</h1>
            
            <form onSubmit={doSubmit}>
                <label>                
                    <p>Username</p>
                    <input type="text" name="username" placeholder="Username"  required onChange={e => setUsername(e.target.value)}/>
                </label>
                <label>                
                    <p>Password</p>
                    <input type="text" name="password" placeholder="Password" required onChange={e => setPassword(e.target.value)} />
                </label>
                <div>
                    <button type="submit">Submit</button>
                </div>
            </form>
        </div>
    );
}

export default Login;