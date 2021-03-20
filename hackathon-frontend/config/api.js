// const ROOT_API = process.env.NUXT_ENV_BACKEND_DOMAIN
const ROOT_API = process.env.NUXT_ENV_BACKEND_DOMAIN_LOCAL

export default {
  car: `${ROOT_API}/v1/api/car`,
  rental: `${ROOT_API}/v1/api/car-rental`,
  customer: `${ROOT_API}/v1/api/customer`,
  status: `${ROOT_API}/v1/api/data-status`,
  category: `${ROOT_API}/v1/api/car-cartegory`,
}
