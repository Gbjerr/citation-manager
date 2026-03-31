import type { TokenPair } from '../../types/types'
import './Navbar.css'

type NavbarProps = {
    isAuthenticated: boolean,
    tokenPair: TokenPair
    clear: () => void
}

export const Navbar = ({isAuthenticated, tokenPair, clear}: NavbarProps) => {

        const handleLogout = async (clear: () => void, tokenPair: TokenPair) => {
        try {
            const headers: Record<string, string> = {"Content-Type": "application/json"};
            if(tokenPair?.accessToken) {
                headers["Authorization"] = `Bearer ${tokenPair.accessToken}`;
            }

            const res = await fetch('http://localhost:8090/api/auth/logout', {
                method: "POST",
                headers: headers,
                body: JSON.stringify({
                    refreshToken: tokenPair.refreshToken
                })
            });
            if(!res.ok) console.log(res.status);

        } catch (e) {
            console.error(e);
        } finally {
            clear();
        }
    }

    return (
        <nav>
            <div className="navLeft">
                <a href="/"><img src='/logo/cm_logo.png' className='logo' /></a>
            </div>
            <div className="navRight">
                {isAuthenticated ? (
                    <button
                        className="logoutBtn"
                        onClick={() => handleLogout(clear, tokenPair)}
                    >
                        Logout
                    </button>
                ) : (
                    <div className='rightBtns'>
                        <button className="logInBtn" >Log in</button>
                        <button className="signInBtn">Sign up</button>
                    </div>
                )}
            </div>
        </nav>
    );
}