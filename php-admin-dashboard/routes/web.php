use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;

Route::get('/', fn() => view('welcome'));

Route::post('/login', function(Request $req) {
$resp = Http::withHeaders([
'X-Tenant-ID' => env('IAM_TENANT_ID')
])->post(env('IAM_API_BASE').'/auth/login', [
'email' => $req->input('email'),
'password' => $req->input('password'),
]);

if (!$resp->ok()) return back()->withErrors(['login' => 'Invalid credentials']);

session(['iam_token' => $resp['access_token']]);
return redirect('/dashboard');
});

Route::get('/dashboard', function() {
$token = session('iam_token');
abort_unless($token, 401);

$me = Http::withHeaders([
'Authorization' => 'Bearer '.$token,
'X-Tenant-ID' => env('IAM_TENANT_ID')
])->get(env('IAM_API_BASE').'/api/users/me');

return view('dashboard', ['profile' => $me->json()]);
});
